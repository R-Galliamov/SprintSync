package com.developers.sprintsync.data.track.service.processing.calculator.pace

import com.developers.sprintsync.core.util.DistanceConverter
import com.developers.sprintsync.data.components.TimeConverter
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



