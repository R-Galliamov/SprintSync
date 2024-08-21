package com.developers.sprintsync.user.model

import com.developers.sprintsync.user.model.chart.chartData.Metric

data class DailyGoal(
    val timestamp: Long,
    val metricType: Metric,
    val value: Float,
)
