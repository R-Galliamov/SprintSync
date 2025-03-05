package com.developers.sprintsync.tracking.data.processing.util.calculator.speed

import javax.inject.Inject

class SpeedCalculatorFactory @Inject constructor() {
    fun getCalculator(unit: SpeedUnit): SpeedCalculatorStrategy =
        when (unit) {
            SpeedUnit.METERS_PER_MINUTES -> SpeedCalculatorStrategy.MetersPerMinuteCalculatorStrategy
        }
}