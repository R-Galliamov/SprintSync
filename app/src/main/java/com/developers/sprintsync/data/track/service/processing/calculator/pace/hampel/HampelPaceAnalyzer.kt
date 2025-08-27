package com.developers.sprintsync.data.track.service.processing.calculator.pace.hampel

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.log.NoopLogger
import com.developers.sprintsync.data.track.service.processing.calculator.DistanceCalculator
import com.developers.sprintsync.data.track.service.processing.calculator.pace.PaceState
import com.developers.sprintsync.data.track.service.processing.calculator.pace.RunPaceAnalyzer
import com.developers.sprintsync.data.track.service.processing.session.TrackPoint
import javax.inject.Inject

class HampelPaceAnalyzer @Inject constructor(
    private val distCalculator: DistanceCalculator,
    private val hampelFilter: HampelFilter,
    private val emaSmoother: EmaSmoother,
    private val maxSpeedMps: Float = 7.5f,     // hard cap for unrealistic speed spikes
    private val log: AppLogger = NoopLogger(),
) : RunPaceAnalyzer {

    private var lastTp: TrackPoint? = null
    private var totalDist = 0f
    private var totalTime = 0f

    override var snapshot: PaceState = PaceState.EMPTY
        private set

    override fun reset() {
        lastTp = null
        totalDist = 0f
        totalTime = 0f
        hampelFilter.clear()
        emaSmoother.reset()
    }

    override fun add(tp: TrackPoint): PaceState {
        val prevTp = lastTp
        lastTp = tp
        if (prevTp == null) return snapshot()

        val dtSec = ((tp.timeMs - prevTp.timeMs).coerceAtLeast(0)) / 1000f
        if (!(dtSec.isFinite()) || dtSec <= 0f) return snapshot()

        val distM = distCalculator.distM(prevTp.location, tp.location)
        if (!(distM.isFinite()) || distM < 0f) return snapshot()

        var segSpeed = distM / dtSec
        if (!segSpeed.isFinite()) return snapshot()
        if (segSpeed < 0f) segSpeed = 0f
        if (segSpeed > maxSpeedMps) segSpeed = maxSpeedMps

        val filtered = hampelFilter.addAndClamp(segSpeed).let { if (it.isNaN()) 0f else it }
        emaSmoother.update(dtSec, filtered)

        if (segSpeed > 0f) totalDist += distM
        totalTime += dtSec

        snapshot = snapshot()
        return snapshot
    }


    private fun snapshot(): PaceState {
        val speed = emaSmoother.value
        val paceMinPerKm = if (speed > 0f) (1000f / speed) / 60f else null
        return PaceState(
            speedMps = speed,
            paceMinPerKm = paceMinPerKm,
            totalDistanceM = totalDist,
            totalTimeSec = totalTime,
        )
    }
}