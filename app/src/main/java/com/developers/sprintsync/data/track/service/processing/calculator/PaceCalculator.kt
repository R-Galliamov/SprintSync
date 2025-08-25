package com.developers.sprintsync.data.track.service.processing.calculator

import com.developers.sprintsync.core.util.DistanceConverter
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.time.TimeConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

class DistanceBufferedPaceAnalyzer @Inject constructor(
    private val paceCalculator: PaceCalculator,
    private val smoothedPaceCalculator: SmoothedPaceCalculator,
    private val log: AppLogger,
    private val bufferDistanceMeters: Float = 10f
) {

    private var accumulatedDistance = 0f
    private var accumulatedTime = 0L

    private val _currentPace = MutableStateFlow(0f)
    val currentPace = _currentPace.asStateFlow()

    private var isFirstBuffer: Boolean = true

    /**
     * Adds a new segment of run
     */
    fun addSegment(coveredMeters: Float, durationMillis: Long) {
        if (coveredMeters < 0f || durationMillis < 0L) {
            log.w("Attempted to add segment with invalid data. Distance: $coveredMeters, Duration: $durationMillis. Ignoring.")
            return
        }

        accumulatedDistance += coveredMeters
        accumulatedTime += durationMillis

        val newPace = calculateCurrentPace()
        _currentPace.update { newPace }

        log.d("Pace updated: $newPace min/km. Accumulated Distance: $accumulatedDistance m, Accumulated Time: $accumulatedTime ms. Is First Buffer: $isFirstBuffer")
    }

    /** Clears the buffer */
    fun resetAccumulatedData() {
        accumulatedDistance = 0f
        accumulatedTime = 0L
    }

    private fun calculateCurrentPace(): Float {
        return if (isFirstBuffer) {
            val smoothedPace = smoothedPaceCalculator.addSample(accumulatedTime, accumulatedDistance)
            if (accumulatedDistance >= bufferDistanceMeters) {
                log.d("First buffer filled (Distance: $accumulatedDistance m). Transitioning to standard buffering.")
                isFirstBuffer = false
                resetAccumulatedData()
            }
            smoothedPace
        } else {
            if (accumulatedDistance >= bufferDistanceMeters) {
                val chunkPace = paceCalculator.getPaceInMinPerKm(accumulatedTime, accumulatedDistance)
                resetAccumulatedData()
                chunkPace
            } else {
                _currentPace.value
            }
        }
    }
}



