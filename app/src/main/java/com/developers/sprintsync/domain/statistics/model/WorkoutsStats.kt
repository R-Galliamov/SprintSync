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
)