package com.developers.sprintsync.tracking.analytics.model

data class FormattedStatistics(
    val runs: String,
    val totalDistance: String,
    val totalKiloCalories: String,
    val longestDistance: String,
    val bestPace: String,
    val maxDuration: FormattedDurationParts,
)
