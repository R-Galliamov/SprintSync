package com.developers.sprintsync.tracking.data.processing.util.calculator.calories

import com.developers.sprintsync.data.user_parameters.cache.CachedUserParameters
import javax.inject.Inject

class CaloriesCalculatorHelper
    @Inject
    constructor(
        private val caloriesCalculator: CaloriesCalculator,
        private val userParameters: CachedUserParameters, // TODO delete cashed parameters at all. Try DI here or something else.
    ) {
        fun calculateBurnedCalories(
            speedInMetersPerMinute: Float,
            durationInHours: Float,
        ): Float {
            val weight = userParameters.userWeightKg
            return caloriesCalculator.calculateBurnedCalories(speedInMetersPerMinute, durationInHours, weight)
        }
    }
