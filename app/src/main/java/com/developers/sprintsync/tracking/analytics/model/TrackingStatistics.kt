package com.developers.sprintsync.tracking.analytics.model

data class TrackingStatistics(
    val runs: Int,
    val totalDistance: Int,
    val totalCalories: Int,
    val longestDistance: Int,
    val bestPace: Float,
    val maxDuration: Long,
) {
    companion object {
        val EMPTY =
            TrackingStatistics(
                runs = 0,
                totalDistance = 0,
                totalCalories = 0,
                longestDistance = 0,
                bestPace = 0f,
                maxDuration = 0L,
            )
    }
}
