package com.developers.sprintsync.domain.workouts_plan.use_case

import com.developers.sprintsync.domain.core.Resource
import com.developers.sprintsync.domain.workouts_plan.model.WorkoutPlan
import com.developers.sprintsync.domain.workouts_plan.repository.WorkoutPlanRepository
import kotlinx.coroutines.flow.Flow

class GetWorkoutPlansUseCase(val repository: WorkoutPlanRepository) {

    operator fun invoke(forceRefresh: Boolean = false): Flow<Resource<List<WorkoutPlan>>> {
        return repository.getWorkoutPlans(forceRefresh)
    }
}