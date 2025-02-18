package com.developers.sprintsync.tracking.data.processing.util.calculator.calories

import com.developers.sprintsync.user_parameters.components.cache.data.model.CachedUserParameters
import javax.inject.Inject

class CaloriesCalculatorHelper
    @Inject
    constructor(
        private val caloriesCalculator: CaloriesCalculator,
        private val userParameters: CachedUserParameters,
    ) {
        fun calculateBurnedCalories(
            speedInMetersPerMinute: Float,
            durationInHours: Float,
        ): Int {
            val weight = userParameters.userWeightKg
            return caloriesCalculator.calculateBurnedCalories(speedInMetersPerMinute, durationInHours, weight)
        }
    }
