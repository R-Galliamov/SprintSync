package com.developers.sprintsync.domain.track.inner_processing.calculator.calories.met

class METCalculator(
    private val vO2CalculatorFactory: VO2CalculatorFactory,
) {
    fun calculateMET(speedInMetersPerMinute: Float): Float =
        vO2CalculatorFactory.getCalculator(speedInMetersPerMinute).calculateVO2(speedInMetersPerMinute) / CONST

    companion object {
        private const val CONST = 3.5f
    }
}
