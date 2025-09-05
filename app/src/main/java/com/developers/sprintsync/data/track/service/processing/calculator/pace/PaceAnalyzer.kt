package com.developers.sprintsync.data.track.service.processing.calculator.pace

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.processing.calculator.DistanceCalculator
import com.developers.sprintsync.data.track.service.processing.session.TrackPoint
import kotlin.math.*

data class PaceSnapshot(
    val paceMpk: Float?,
    val totalDistanceM: Float,        // accumulated distance, meters
    val totalTimeSec: Float,          // total elapsed time, seconds
) {
    companion object {
        val EMPTY = PaceSnapshot(null, 0f, 0f)
    }
}

interface RunPaceAnalyzer {

    val snapshot: PaceSnapshot

    fun reset()

    fun add(tp: TrackPoint): PaceSnapshot
}


class RollingRunPaceAnalyzer(
    private val windowSeconds: Long,
    private val lagSeconds: Long,
    private val distCalc: DistanceCalculator,
    private val log: AppLogger,
) : RunPaceAnalyzer {

    private val points: ArrayDeque<TrackPoint> = ArrayDeque()
    private var startTimeMs: Long? = null
    private var lastPoint: TrackPoint? = null
    private var totalDistanceMAcc: Double = 0.0

    override var snapshot: PaceSnapshot = PaceSnapshot.EMPTY
        private set

    override fun reset() {
        points.clear()
        startTimeMs = null
        lastPoint = null
        totalDistanceMAcc = 0.0
        snapshot = PaceSnapshot.EMPTY
    }

    override fun add(tp: TrackPoint): PaceSnapshot {
        if (startTimeMs == null) startTimeMs = tp.timeMs

        lastPoint?.let { prev ->
            if (tp.timeMs > prev.timeMs) {
                totalDistanceMAcc += distCalc.distM(tp.location, prev.location)
            }
        }
        lastPoint = tp

        points.addLast(tp)
        prune(tp.timeMs)

        val totalTimeSec = startTimeMs?.let { ((tp.timeMs - it).coerceAtLeast(0L) / 1000f) } ?: 0f

        val haveWindow = startTimeMs?.let { tp.timeMs - it >= (windowSeconds + lagSeconds) * 1000L } == true
        if (!haveWindow) {
            snapshot = PaceSnapshot(
                paceMpk = null,
                totalDistanceM = totalDistanceMAcc.toFloat(),
                totalTimeSec = totalTimeSec
            )
            return snapshot
        }

        val speed = windowSpeedMps(tp.timeMs).toFloat()
        val paceMpk = if (speed > 0f) (1000f / speed) / 60f else null

        snapshot = PaceSnapshot(
            paceMpk = paceMpk,
            totalDistanceM = totalDistanceMAcc.toFloat(),
            totalTimeSec = totalTimeSec
        )

        log.d("Pace snapshot: $snapshot")
        return snapshot
    }

    private fun prune(nowMs: Long) {
        val keepHorizonMs = (windowSeconds + lagSeconds + 5) * 1000L
        val cutoff = nowMs - keepHorizonMs
        while (points.isNotEmpty() && points.first().timeMs < cutoff) {
            points.removeFirst()
        }
    }

    private fun windowSpeedMps(nowMs: Long): Double {
        val tStart = nowMs - (windowSeconds + lagSeconds) * 1000L
        val tEnd = nowMs - lagSeconds * 1000L

        val stable = points.asSequence().filter { it.timeMs <= tEnd }.toList()
        if (stable.size < 2) return 0.0

        var distM = 0.0
        for (i in 0 until stable.size - 1) {
            val a = stable[i]
            val b = stable[i + 1]
            val segStart = max(a.timeMs, tStart)
            val segEnd = min(b.timeMs, tEnd)
            if (segEnd <= segStart) continue

            val whole = (b.timeMs - a.timeMs).toDouble()
            if (whole <= 0.0) continue

            val fraction = (segEnd - segStart) / whole
            val d = distCalc.distM(a.location, b.location) * fraction
            distM += d
        }

        return distM / windowSeconds.toDouble()
    }
}
