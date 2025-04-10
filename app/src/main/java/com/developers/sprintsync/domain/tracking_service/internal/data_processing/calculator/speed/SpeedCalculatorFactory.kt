package com.developers.sprintsync.domain.tracking_service.internal.data_processing.calculator.speed

import javax.inject.Inject

class SpeedCalculatorFactory @Inject constructor() {
    fun getCalculator(unit: SpeedUnit): SpeedCalculatorStrategy =
        when (unit) {
            SpeedUnit.METERS_PER_MINUTES -> SpeedCalculatorStrategy.MetersPerMinuteCalculatorStrategy
        }
}