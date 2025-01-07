package com.developers.sprintsync.core.components.goal.data.model

data class DailyGoal(
    val id: Int = 0,
    val timestamp: Long,
    val metricType: Metric,
    val value: Float,
)
