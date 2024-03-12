package com.developers.sprintsync.tracking.util.calculator

import com.developers.sprintsync.tracking.mapper.indicator.DistanceMapper
import com.developers.sprintsync.tracking.mapper.indicator.TimeMapper

object PaceCalculator {
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
