package com.developers.sprintsync.core.tracking_service.data.processing.util.calculator.speed

class SpeedCalculatorFactory {
    fun getCalculator(unit: SpeedUnit): SpeedCalculatorStrategy =
        when (unit) {
            SpeedUnit.METERS_PER_MINUTES -> SpeedCalculatorStrategy.MetersPerMinuteCalculatorStrategy
        }
}