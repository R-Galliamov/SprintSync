package com.developers.sprintsync.core.tracking_service.data.processing.util.calculator.calories

import com.developers.sprintsync.core.tracking_service.data.processing.util.calculator.calories.met.METCalculator
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
            val met = mETCalculator.calculateMET(speedInMetersPerMinute)
            return calculateBurnedCaloriesFromMet(met, durationInHours, weightInKilos)
        }

        private fun calculateBurnedCaloriesFromMet(
            met: Float,
            durationInHours: Float,
            weightInKilos: Float,
        ): Int = (met * durationInHours * weightInKilos).roundToInt()
    }
