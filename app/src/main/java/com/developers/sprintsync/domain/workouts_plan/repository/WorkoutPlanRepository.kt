package com.developers.sprintsync.domain.workouts_plan.repository

import com.developers.sprintsync.domain.core.Resource
import com.developers.sprintsync.domain.workouts_plan.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface WorkoutPlanRepository {

    fun getWorkoutPlans(forceRefresh: Boolean): Flow<Resource<List<WorkoutPlan>>>
}