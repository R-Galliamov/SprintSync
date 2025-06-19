package com.developers.sprintsync.data.track.service.processing.calculator.calories.met

/**
 * Calculates MET (Metabolic Equivalent of Task) values based on speed.
 */
class METCalculator(
    private val vo2CalculatorFactory: VO2CalculatorFactory,
) {
    /**
     * Calculates MET value for a given speed.
     * @param speedMPM Speed in meters per minute.
     * @return MET value.
     */
    fun calculateMet(speedMPM: Float): Float =
        vo2CalculatorFactory.getCalculator(speedMPM).calculateVO2(speedMPM) / Constants.DIVIDER

    private object Constants {
        const val DIVIDER = 3.5f
    }
}
