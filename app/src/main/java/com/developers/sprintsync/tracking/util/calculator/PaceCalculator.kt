package com.developers.sprintsync.tracking.util.calculator

import com.developers.sprintsync.tracking.mapper.indicator.DistanceMapper
import com.developers.sprintsync.tracking.mapper.indicator.TimeMapper

object PaceCalculator {
    fun getPaceInMinPerKm(
        durationMillis: Long,
        coveredMeters: Int,
    ): Float {
        val minutes = TimeMapper.millisToMinutes(durationMillis)
        val kilometers = DistanceMapper.metersToKilometers(coveredMeters)
        return (minutes / kilometers)
    }
}
