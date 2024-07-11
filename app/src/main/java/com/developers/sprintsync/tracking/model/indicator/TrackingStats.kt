package com.developers.sprintsync.tracking.model.indicator

data class TrackingStats(
    val runs: Int,
    val totalDistance: Int,
    val totalKcal: Int,
    val longestDistance: Int,
    val bestPace: Int,
    val maxDuration: Int,
)
