package com.developers.sprintsync.presentation.workouts_statistics.data

import com.developers.sprintsync.presentation.workouts_statistics.chart.data.DailyValues
import com.developers.sprintsync.domain.goal.model.Metric

data class ChartDataUpdateEvent(
    val metric: Metric,
    val dailyValues: List<DailyValues>,
    val referenceTimestamp: Long,
)
