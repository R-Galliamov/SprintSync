package com.developers.sprintsync.statistics.domain.goal

import com.developers.sprintsync.statistics.domain.chart.data.Metric

data class DailyGoal(
    val id: Int = 0,
    val timestamp: Long,
    val metricType: Metric,
    val value: Float,
)
