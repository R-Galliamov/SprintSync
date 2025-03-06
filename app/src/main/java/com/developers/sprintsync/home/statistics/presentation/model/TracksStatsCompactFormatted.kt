package com.developers.sprintsync.home.statistics.presentation.model

import com.developers.sprintsync.core.util.time.TimeParts

data class TracksStatsCompactFormatted(
    val runs: String,
    val totalDistance: String,
    val totalKiloCalories: String,
    val longestDistance: String,
    val bestPace: String,
    val maxDuration: TimeParts,
)
