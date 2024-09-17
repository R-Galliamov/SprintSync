package com.developers.sprintsync.statistics.model.goal

import com.developers.sprintsync.statistics.model.chart.chartData.Metric

data class DailyGoal(
    val id: Int = 0,
    val timestamp: Long,
    val metricType: Metric,
    val value: Float,
)
