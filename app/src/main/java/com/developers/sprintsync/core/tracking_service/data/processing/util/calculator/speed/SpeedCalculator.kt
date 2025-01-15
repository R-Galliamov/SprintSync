package com.developers.sprintsync.core.tracking_service.data.processing.util.calculator.speed

class SpeedCalculator(
    private val factory: SpeedCalculatorFactory,
) {
    fun calculateSpeed(
        durationMillis: Long,
        distanceMeters: Float,
        toUnit: SpeedUnit,
    ): Float {
        val calculator = factory.getCalculator(toUnit)
        return calculator.calculate(durationMillis, distanceMeters)
    }
}
