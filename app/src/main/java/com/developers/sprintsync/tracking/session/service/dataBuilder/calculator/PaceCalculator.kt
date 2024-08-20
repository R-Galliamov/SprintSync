package com.developers.sprintsync.tracking.session.service.dataBuilder.calculator

import com.developers.sprintsync.tracking.analytics.dataManager.converter.TimeConverter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DistanceFormatter

class PaceCalculator {
    companion object {
        fun getPaceInMinPerKm(
            durationMillis: Long,
            coveredMeters: Int,
        ): Float {
            require(durationMillis >= 0) { "durationMillis must be non-negative" }
            require(coveredMeters > 0) { "coveredMeters must be positive" }
            val minutes = TimeConverter.millisToMinutes(durationMillis)
            val kilometers = DistanceFormatter.metersToKilometers(coveredMeters)
            return (minutes / kilometers)
        }
    }
}
