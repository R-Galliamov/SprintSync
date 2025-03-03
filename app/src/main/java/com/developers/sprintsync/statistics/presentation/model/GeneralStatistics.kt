package com.developers.sprintsync.statistics.presentation.model

data class GeneralStatistics(
    val totalWorkouts: String,
    val maxWorkoutStreak: String,
    val totalWorkoutDays: String,
    val totalDistance: String,
    val totalDuration: String,
    val avgPace: String,
    val bestPace: String,
    val totalBurnedKcal: String,
) {
    companion object {
        val EMPTY =
            GeneralStatistics(
                totalWorkouts = "",
                maxWorkoutStreak = "",
                totalWorkoutDays = "",
                totalDistance = "",
                totalDuration = "",
                avgPace = "",
                bestPace = "",
                totalBurnedKcal = "",
            )
    }
}
