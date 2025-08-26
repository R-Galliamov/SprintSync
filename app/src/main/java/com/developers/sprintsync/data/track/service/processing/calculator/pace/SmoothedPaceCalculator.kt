package com.developers.sprintsync.data.track.service.processing.calculator.pace

import javax.inject.Inject

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