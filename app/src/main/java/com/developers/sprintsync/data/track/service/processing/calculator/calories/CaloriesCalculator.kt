package com.developers.sprintsync.data.track.service.processing.calculator.calories

import com.developers.sprintsync.data.track.service.processing.calculator.calories.met.METProvider
import com.developers.sprintsync.data.track.service.processing.calculator.calories.met.VO2CalculatorFactory

/**
 * Calculates calories burned based on MET (Metabolic Equivalent of Task) values.
 */
class CaloriesCalculator {
    private val metCalculator = METProvider(VO2CalculatorFactory())

    /**
     * Calculates calories burned using speed, duration, and user's weight.
     * @param speedMPM Speed in meters per minute.
     * @param durationHours Duration of activity in hours.
     * @param weightKg User's weight in kilograms.
     * @return Total calories burned.
     * @throws IllegalArgumentException if speedMPM is negative or durationHours is not positive.
     */
    fun totalCalories(
        speedMPM: Float,
        durationHours: Float,
        weightKg: Float,
    ): Float {
        require(speedMPM >= 0) { "Speed must be non-negative" }
        require(durationHours > 0) { "Duration must be greater than zero" }
        val met = metCalculator.metForSpeed(speedMPM)
        return calculateCaloriesFromMet(met, durationHours, weightKg)
    }

    /**
     * Computes calories burned from MET value, duration, and weight.
     * @param met MET value for the activity.
     * @param durationHours Duration of activity in hours.
     * @param weightKg User's weight in kilograms.
     * @return Calories burned.
     */
    private fun calculateCaloriesFromMet(
        met: Float,
        durationHours: Float,
        weightKg: Float,
    ): Float = (met * durationHours * weightKg)
}
