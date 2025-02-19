package com.developers.sprintsync.tracking.data.processing.util.calculator.calories

import com.developers.sprintsync.tracking.data.processing.util.calculator.calories.met.METCalculator
import jakarta.inject.Inject
import kotlin.math.roundToInt

class CaloriesCalculator
    @Inject
    constructor(
        private val mETCalculator: METCalculator,
    ) {
        fun calculateBurnedCalories(
            speedInMetersPerMinute: Float,
            durationInHours: Float,
            weightInKilos: Float,
        ): Int {
            require( speedInMetersPerMinute >= 0) { "Speed must be non-negative" }
            require(durationInHours > 0) { "Duration must be greater than zero" }
            val met = mETCalculator.calculateMET(speedInMetersPerMinute)
            return calculateBurnedCaloriesFromMet(met, durationInHours, weightInKilos)
        }

        private fun calculateBurnedCaloriesFromMet(
            met: Float,
            durationInHours: Float,
            weightInKilos: Float,
        ): Int = (met * durationInHours * weightInKilos).roundToInt()
    }
