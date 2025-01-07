package com.developers.sprintsync.core.tracking_service.data.processing.util.calculator

import com.developers.sprintsync.core.components.track.domain.metrics_converter.DistanceConverter
import com.developers.sprintsync.core.components.time.utils.TimeConverter

class PaceCalculator {
    companion object {
        /**
         * Calculates pace in minutes per kilometer.
         */
        fun getPace(
            durationMillis: Long,
            coveredMeters: Int,
        ): Float {
            require(durationMillis >= 0) { "durationMillis must be non-negative" }
            require(coveredMeters > 0) { "coveredMeters must be positive" }
            val minutes = TimeConverter.millisToMinutes(durationMillis)
            val kilometers = DistanceConverter.metersToKilometers(coveredMeters)
            return (minutes / kilometers)
        }
    }
}
