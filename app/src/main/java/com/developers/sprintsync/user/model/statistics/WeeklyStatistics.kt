package com.developers.sprintsync.user.model.statistics

data class WeeklyStatistics(
    val workouts: String,
    val workoutDays: String,
    val totalDistance: String,
    val totalDuration: String,
    val bestDistance: String,
    val bestDuration: String,
    val avgPace: String,
    val bestPace: String,
    val totalCalories: String,
) {
    companion object {
        val EMPTY =
            WeeklyStatistics(
                workouts = "",
                workoutDays = "",
                totalDistance = "",
                totalDuration = "",
                bestDistance = "",
                bestDuration = "",
                avgPace = "",
                bestPace = "",
                totalCalories = "",
            )
    }
}
