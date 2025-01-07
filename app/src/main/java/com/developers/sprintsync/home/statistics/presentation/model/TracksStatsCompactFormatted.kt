package com.developers.sprintsync.home.statistics.presentation.model

import com.developers.sprintsync.core.components.track.presentation.model.FormattedDurationParts

data class TracksStatsCompactFormatted(
    val runs: String,
    val totalDistance: String,
    val totalKiloCalories: String,
    val longestDistance: String,
    val bestPace: String,
    val maxDuration: FormattedDurationParts,
)
