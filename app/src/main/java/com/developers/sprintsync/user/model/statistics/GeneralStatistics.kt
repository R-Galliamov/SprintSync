package com.developers.sprintsync.user.model.statistics

data class GeneralStatistics(
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
