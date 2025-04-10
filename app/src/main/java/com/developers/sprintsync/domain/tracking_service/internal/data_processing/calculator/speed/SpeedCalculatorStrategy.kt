package com.developers.sprintsync.domain.tracking_service.internal.data_processing.calculator.speed

import com.developers.sprintsync.core.util.time.TimeConverter

sealed interface SpeedCalculatorStrategy {
    fun calculate(
        durationMillis: Long,
        distanceMeters: Float,
    ): Float

    data object MetersPerMinuteCalculatorStrategy : SpeedCalculatorStrategy {
        override fun calculate(
            durationMillis: Long,
            distanceMeters: Float,
        ): Float {
            require(durationMillis > 0) { "durationMillis must be positive" }
            require(distanceMeters >= 0) { "distanceMeters must be non-negative" }
            val minutes = TimeConverter.convertFromMillis(durationMillis, TimeConverter.TimeUnit.MINUTES)
            return distanceMeters / minutes
        }
    }
}
