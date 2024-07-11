package com.developers.sprintsync.tracking.util.calculator

import com.developers.sprintsync.tracking.util.mapper.indicator.DistanceMapper
import com.developers.sprintsync.tracking.util.mapper.indicator.TimeMapper

class PaceCalculator {
    companion object {
        fun getPaceInMinPerKm(
            durationMillis: Long,
            coveredMeters: Int,
        ): Float {
            require(durationMillis >= 0) { "durationMillis must be non-negative" }
            require(coveredMeters > 0) { "coveredMeters must be positive" }
            val minutes = TimeMapper.millisToMinutes(durationMillis)
            val kilometers = DistanceMapper.metersToKilometers(coveredMeters)
            return (minutes / kilometers)
        }
    }
}
