package com.developers.sprintsync.domain.track.inner_processing.calculator.calories

class UserCaloriesCalculator(
    private val caloriesCalculator: MetCaloriesCalculator,
) {
    fun calculateBurnedCalories(
        userWeightKilos: Float,
        speedInMetersPerMinute: Float,
        durationInHours: Float,
    ): Float = caloriesCalculator.calculateBurnedCalories(speedInMetersPerMinute, durationInHours, userWeightKilos)
}
