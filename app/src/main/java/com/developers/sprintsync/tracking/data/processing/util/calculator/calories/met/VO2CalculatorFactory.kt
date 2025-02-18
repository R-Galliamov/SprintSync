package com.developers.sprintsync.tracking.data.processing.util.calculator.calories.met

import jakarta.inject.Inject

class VO2CalculatorFactory
    @Inject
    constructor() {
        private val calculatorMapping: List<Pair<ClosedFloatingPointRange<Float>, VO2Calculator>> =
            listOf(
                MIN_SPEED..SPEED_THRESHOLD to VO2Calculator.LowActivityVO2Calculator,
                SPEED_THRESHOLD..Float.MAX_VALUE to VO2Calculator.HighActivityVO2Calculator,
            )

        fun getCalculator(speedInMetersPerMinute: Float): VO2Calculator =
            calculatorMapping.first { (range, _) -> speedInMetersPerMinute in range }.second

        companion object {
            private const val MIN_SPEED = 0f
            private const val SPEED_THRESHOLD = 134.12f
        }
    }
