package com.developers.sprintsync.domain.workouts_plan.model

data class WorkoutPlan(
    val id: Int,
    val startDate: Long,
    val title: String,
    val description: String,
    val days: List<PlanDay>
)

sealed class PlanDay {

    data object Rest : PlanDay()

    data class WorkoutSession(
        val id: Int,
        val title: String,
        val description: String,
        val targets: Map<Metrics, Float>,
    ) : PlanDay()
}

sealed class WorkoutTarget {
    data class Calories(val value: Int) : WorkoutTarget()
    data class Distance(val value: Int) : WorkoutTarget()
    data class Duration(val value: Int) : WorkoutTarget()
    data class Pace(val value: Int) : WorkoutTarget()
}

enum class Metrics { CALORIES, DISTANCE, DURATION, PACE }
