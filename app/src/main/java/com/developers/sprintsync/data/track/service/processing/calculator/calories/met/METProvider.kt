package com.developers.sprintsync.data.track.service.processing.calculator.calories.met

import com.developers.sprintsync.data.track.service.processing.calculator.calories.met.PhysiologyConstants.RESTING_VO2

/**
 * Calculates MET (Metabolic Equivalent of Task) values based on speed.
 */
class METProvider(
    private val vo2CalculatorFactory: VO2CalculatorFactory,
) {
    /**
     * Calculates MET value for a given speed.
     * @param speedMPM Speed in meters per minute.
     * @return MET value.
     */
    fun metForSpeed(speedMPM: Float): Float =
        vo2CalculatorFactory.getCalculator(speedMPM).calculateVO2(speedMPM) / RESTING_VO2
}
