package com.developers.sprintsync.data.track.service.processing.calculator.calories

import com.developers.sprintsync.data.track.service.processing.calculator.calories.met.METCalculator
import com.developers.sprintsync.data.track.service.processing.calculator.calories.met.VO2CalculatorFactory

class MetCaloriesCalculator {
    private val mETCalculator = METCalculator(VO2CalculatorFactory())

    fun calculateBurnedCalories(
        speedInMetersPerMinute: Float,
        durationInHours: Float,
        weightInKilos: Float,
    ): Float {
        require(speedInMetersPerMinute >= 0) { "Speed must be non-negative" }
        require(durationInHours > 0) { "Duration must be greater than zero" }
        val met = mETCalculator.calculateMET(speedInMetersPerMinute)
        return calculateBurnedCaloriesFromMet(met, durationInHours, weightInKilos)
    }

    private fun calculateBurnedCaloriesFromMet(
        met: Float,
        durationInHours: Float,
        weightInKilos: Float,
    ): Float = (met * durationInHours * weightInKilos)
}
