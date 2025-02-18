package com.developers.sprintsync.tracking.data.processing.util.calculator.calories.met

import jakarta.inject.Inject

class METCalculator
    @Inject
    constructor(
        private val vO2CalculatorFactory: VO2CalculatorFactory,
    ) {
        fun calculateMET(speedInMetersPerMinute: Float): Float =
            vO2CalculatorFactory.getCalculator(speedInMetersPerMinute).calculateVO2(speedInMetersPerMinute) / CONST

        companion object {
            private const val CONST = 3.5f
        }
    }
