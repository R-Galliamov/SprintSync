package com.developers.sprintsync.data.track.service.processing.calculator.calories

class UserCaloriesCalculator(
    private val caloriesCalculator: MetCaloriesCalculator,
) {
    fun calculateBurnedCalories(
        userWeightKilos: Float,
        speedInMetersPerMinute: Float,
        durationInHours: Float,
    ): Float = caloriesCalculator.calculateBurnedCalories(speedInMetersPerMinute, durationInHours, userWeightKilos)
}
