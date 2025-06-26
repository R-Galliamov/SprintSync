package com.developers.sprintsync.domain.workouts_plan.model

data class WorkoutPlan(
    val id: String,
    val owner: String,
    val startDate: Long,
    val title: String,
    val description: String,
    val days: List<PlanDay>
)

sealed class PlanDay {

    data object Rest : PlanDay()

    data class WorkoutSession(
        val id: Int,
        val runDay: Int,
        val title: String,
        val description: String,
        val targets: Map<Metric, Float>,
    ) : PlanDay()
}

enum class Metric { CALORIES, DISTANCE, DURATION }

