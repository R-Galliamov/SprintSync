package com.developers.sprintsync.domain.workouts_plan.model

data class DailyGoal(
    val id: Int = 0,
    val timestamp: Long,
    val metricType: Metric,
    val value: Float,
)
