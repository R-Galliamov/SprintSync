package com.developers.sprintsync.domain.statistics.model

data class WorkoutsStats(
    val totalWorkouts: Int,
    val totalWorkoutDays: Int,
    val totalDistanceMeters: Float,
    val totalDurationMillis: Long,
    val longestDistanceMeters: Float,
    val longestDurationMillis: Long,
    val avgPaceMPKm: Float,
    val peakPaceMPKm: Float,
    val totalCalories: Float,
) {
    companion object {
        val EMPTY = WorkoutsStats(
            totalWorkouts = 0,
            totalWorkoutDays = 0,
            totalDistanceMeters = 0f,
            totalDurationMillis = 0L,
            longestDistanceMeters = 0f,
            longestDurationMillis = 0L,
            avgPaceMPKm = 0f,
            peakPaceMPKm = 0f,
            totalCalories = 0f
        )
    }
}