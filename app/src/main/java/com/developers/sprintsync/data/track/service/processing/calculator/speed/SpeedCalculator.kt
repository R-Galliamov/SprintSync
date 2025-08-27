package com.developers.sprintsync.data.track.service.processing.calculator.speed

import javax.inject.Inject

class SpeedCalculator @Inject constructor() {
    private val factory = SpeedCalculatorFactory()

    fun calculateSpeed(
        durationMillis: Long,
        distanceMeters: Float,
        toUnit: SpeedUnit,
    ): Float {
        val calculator = factory.getCalculator(toUnit)
        return calculator.calculate(durationMillis, distanceMeters)
    }
}

enum class SpeedUnit {
    METERS_PER_MINUTES,
}
