package com.developers.sprintsync.data.track.service.processing.calculator

import com.developers.sprintsync.core.util.DistanceConverter
import com.developers.sprintsync.core.util.time.TimeConverter
import javax.inject.Inject

class PaceCalculator @Inject constructor(
    private val distanceConverter: DistanceConverter,
) {
    /**
     * Calculates pace in minutes per kilometer.
     */
    fun getPaceInMinPerKm(
        durationMillis: Long,
        coveredMeters: Float,
    ): Float {
        require(durationMillis >= 0) { "durationMillis must be non-negative" }
        require(coveredMeters > 0) { "coveredMeters must be positive" }
        val minutes = TimeConverter.convertFromMillis(durationMillis, TimeConverter.TimeUnit.MINUTES)
        val kilometers = distanceConverter.convert(coveredMeters, DistanceConverter.Unit.M, DistanceConverter.Unit.KM)
        return (minutes / kilometers)
    }
}

/**
 * Calculates smoothed pace over a sliding window of samples.
 */
class SmoothedPaceCalculator @Inject constructor(
    private val paceCalculator: PaceCalculator,
    private val windowSize: Int,
) {
    private val buffer = ArrayDeque<Float>()

    fun addSample(durationMillis: Long, coveredMeters: Float): Float {
        val pace = paceCalculator.getPaceInMinPerKm(durationMillis, coveredMeters)
        buffer.addLast(pace)
        if (buffer.size > windowSize) buffer.removeFirst()
        return buffer.average().toFloat()
    }
}

/**
 *  Calculates pace based on a rolling buffer of recent distance segments.
 */
class DistanceBufferedPaceCalculator @Inject constructor(
    private val paceCalculator: PaceCalculator,
    private val bufferDistanceMeters: Float = 200f
) {

    private data class PaceSegment(val distanceMeters: Float, val timeMillis: Long)

    private val segments = mutableListOf<PaceSegment>()
    private var accumulatedDistance = 0f
    private var accumulatedTime = 0L

    /**
     * Adds a new segment of run
     */
    fun addSegment(coveredMeters: Float, durationMillis: Long) {
        segments.add(PaceSegment(coveredMeters, durationMillis))
        accumulatedDistance += coveredMeters
        accumulatedTime += durationMillis

        // Remove oldest segments if buffer exceeded
        while (accumulatedDistance > bufferDistanceMeters && segments.isNotEmpty()) {
            val first = segments.first()
            accumulatedDistance -= first.distanceMeters
            accumulatedTime -= first.timeMillis
            segments.removeAt(0)
        }
    }

    /**
     * Returns current pace in min/km, or null if not enough data
     */
    fun getCurrentPaceMinPerKm(): Float? {
        if (accumulatedDistance <= 0f) return null
        return paceCalculator.getPaceInMinPerKm(accumulatedTime, accumulatedDistance)
    }

    /** Clears the buffer */
    fun reset() {
        segments.clear()
        accumulatedDistance = 0f
        accumulatedTime = 0L
    }
}


