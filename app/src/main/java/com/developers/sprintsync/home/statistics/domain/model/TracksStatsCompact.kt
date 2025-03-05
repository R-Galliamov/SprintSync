package com.developers.sprintsync.home.statistics.domain.model

data class TracksStatsCompact(
    val runs: Int,
    val totalDistance: Float,
    val totalCalories: Float,
    val longestDistance: Float,
    val bestPace: Float,
    val maxDuration: Long,
) {
    companion object {
        val EMPTY =
            TracksStatsCompact(
                runs = 0,
                totalDistance = 0f,
                totalCalories = 0f,
                longestDistance = 0f,
                bestPace = 0f,
                maxDuration = 0L,
            )
    }
}
