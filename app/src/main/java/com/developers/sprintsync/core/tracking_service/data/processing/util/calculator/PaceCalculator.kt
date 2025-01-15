package com.developers.sprintsync.core.tracking_service.data.processing.util.calculator

import com.developers.sprintsync.core.components.time.utils.TimeConverter
import com.developers.sprintsync.core.components.track.domain.metrics_converter.DistanceConverter
import javax.inject.Inject

class PaceCalculator
    @Inject
    constructor() {
        /**
         * Calculates pace in minutes per kilometer.
         */
        fun getPaceInMinPerKm(
            durationMillis: Long,
            coveredMeters: Float,
        ): Float {
            require(durationMillis >= 0) { "durationMillis must be non-negative" }
            require(coveredMeters > 0) { "coveredMeters must be positive" }
            val minutes = TimeConverter.millisToMinutes(durationMillis)
            val kilometers = DistanceConverter.metersToKilometers(coveredMeters)
            return (minutes / kilometers)
        }
    }
