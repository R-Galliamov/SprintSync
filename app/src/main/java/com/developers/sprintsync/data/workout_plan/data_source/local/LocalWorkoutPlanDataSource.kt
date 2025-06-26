package com.developers.sprintsync.data.workout_plan.data_source.local

import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDto
import kotlinx.coroutines.flow.Flow

interface LocalWorkoutPlanDataSource {
    suspend fun insert(dto: WorkoutPlanDto)
    suspend fun insertAll(dtos: List<WorkoutPlanDto>)


    fun getAllFlow(): Flow<List<WorkoutPlanDto>>
    suspend fun getAll(): List<WorkoutPlanDto>

    fun getAllSystemPlans(): Flow<List<WorkoutPlanDto>>
    suspend fun getAllSystemPlansOnce(): List<WorkoutPlanDto>

    fun getAllByOwner(owner: String): Flow<List<WorkoutPlanDto>>
    suspend fun getAllByOwnerOnce(owner: String): List<WorkoutPlanDto>

    suspend fun getById(id: String): WorkoutPlanDto?

    suspend fun deleteById(id: String)
    suspend fun delete(dto: WorkoutPlanDto) = deleteById(dto.id)
}