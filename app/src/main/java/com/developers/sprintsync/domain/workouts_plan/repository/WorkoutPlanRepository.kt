package com.developers.sprintsync.domain.workouts_plan.repository

import com.developers.sprintsync.domain.core.Resource
import com.developers.sprintsync.domain.core.ResourceList
import com.developers.sprintsync.domain.workouts_plan.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

interface WorkoutPlanRepository {

    suspend fun save(plan: WorkoutPlan): Resource.Result<Unit>
    suspend fun saveAll(plans: List<WorkoutPlan>): Resource.Result<Unit>

    fun getAllSystemPlans(forceRefresh: Boolean): Flow<ResourceList<WorkoutPlan>>
    fun getAllByOwner(owner: String, forceRefresh: Boolean): Flow<ResourceList<WorkoutPlan>>

    suspend fun getById(id: String): Resource.Result<WorkoutPlan>

    suspend fun deleteById(id: String): Resource.Result<Unit>
    suspend fun delete(plan: WorkoutPlan): Resource.Result<Unit> = deleteById(plan.id)
}
