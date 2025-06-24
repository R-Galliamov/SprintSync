package com.developers.sprintsync.domain.workouts_plan.util

import com.developers.sprintsync.domain.workouts_plan.model.WorkoutPlan
import java.util.concurrent.TimeUnit

fun WorkoutPlan.dateOfPlanDay(index: Int): Long {
    require(index in days.indices) { "Invalid day index" }
    return startDate + TimeUnit.DAYS.toMillis(index.toLong())
}