package com.developers.sprintsync.core.tracking_service.data.processing.util.calculator.speed

import com.developers.sprintsync.core.components.time.utils.TimeConverter

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
            val minutes = TimeConverter.millisToMinutes(durationMillis)
            return distanceMeters / minutes
        }
    }
}
