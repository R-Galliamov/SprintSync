package com.developers.sprintsync.tracking.data.processing.util.calculator.speed

import javax.inject.Inject

class SpeedCalculator
    @Inject
    constructor(
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
