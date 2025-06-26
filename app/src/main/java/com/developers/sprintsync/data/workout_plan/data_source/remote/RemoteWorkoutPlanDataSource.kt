package com.developers.sprintsync.data.workout_plan.data_source.remote

import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDto

interface RemoteWorkoutPlanDataSource {
    suspend fun save(dto: WorkoutPlanDto)
    suspend fun saveAll(dtos: List<WorkoutPlanDto>)
    suspend fun getById(id: String): WorkoutPlanDto?
    suspend fun getAllSystemPlans(): List<WorkoutPlanDto>
    suspend fun getAllOwnerPlans(owner: String): List<WorkoutPlanDto>
    suspend fun deleteById(id: String)
    suspend fun delete(dto: WorkoutPlanDto) = deleteById(dto.id)
}