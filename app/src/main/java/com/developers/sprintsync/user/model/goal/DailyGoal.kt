package com.developers.sprintsync.user.model.goal

import com.developers.sprintsync.user.model.chart.chartData.Metric

data class DailyGoal(
    val id: Int = 0,
    val timestamp: Long,
    val metricType: Metric,
    val value: Float,
)
