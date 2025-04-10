package com.developers.sprintsync.domain.tracking_service.internal.data_processing.calculator

import com.developers.sprintsync.core.util.MetersToKilometersConverter
import com.developers.sprintsync.core.util.time.TimeConverter
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
            val minutes = TimeConverter.convertFromMillis(durationMillis, TimeConverter.TimeUnit.MINUTES)
            val kilometers = MetersToKilometersConverter.convert(coveredMeters)
            return (minutes / kilometers)
        }
    }
