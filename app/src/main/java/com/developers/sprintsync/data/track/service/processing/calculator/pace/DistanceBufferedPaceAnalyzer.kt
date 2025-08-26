package com.developers.sprintsync.data.track.service.processing.calculator.pace

import com.developers.sprintsync.core.util.log.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

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