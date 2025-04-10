package com.developers.sprintsync.domain.tracking_service.internal.data_processing.calculator.speed

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
