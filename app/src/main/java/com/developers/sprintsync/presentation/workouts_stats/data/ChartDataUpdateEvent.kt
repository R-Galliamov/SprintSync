package com.developers.sprintsync.presentation.workouts_stats.data

import com.developers.sprintsync.presentation.workouts_stats.chart.data.DailyValues
import com.developers.sprintsync.presentation.components.Metric

data class ChartDataUpdateEvent(
    val metric: Metric,
    val dailyValues: List<DailyValues>,
    val referenceTimestamp: Long,
)
