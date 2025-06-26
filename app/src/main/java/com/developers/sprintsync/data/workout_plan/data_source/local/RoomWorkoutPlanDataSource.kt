package com.developers.sprintsync.data.workout_plan.data_source.local

import com.developers.sprintsync.data.workout_plan.data_source.constants.OwnerConstants
import com.developers.sprintsync.data.workout_plan.dao.WorkoutPlanDao
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomWorkoutPlanDataSource @Inject constructor(
    private val dao: WorkoutPlanDao
) : LocalWorkoutPlanDataSource {

    override suspend fun insert(dto: WorkoutPlanDto) {
        dao.insert(dto)
    }

    override suspend fun insertAll(dtos: List<WorkoutPlanDto>) {
        dao.insertAll(dtos)
    }

    override fun getAllByOwner(owner: String): Flow<List<WorkoutPlanDto>> =
        dao.getAllByOwner(owner)

    override suspend fun getAllByOwnerOnce(owner: String): List<WorkoutPlanDto> =
        dao.getAllByOwnerOnce(owner)

    override fun getAllSystemPlans(): Flow<List<WorkoutPlanDto>> =
        dao.getAllByOwner(OwnerConstants.SYSTEM)

    override suspend fun getAllSystemPlansOnce(): List<WorkoutPlanDto> =
        dao.getAllByOwnerOnce(OwnerConstants.SYSTEM)

    override fun getAllFlow(): Flow<List<WorkoutPlanDto>> =
        dao.getAllFlow()

    override suspend fun getAll(): List<WorkoutPlanDto> =
        dao.getAll()

    override suspend fun getById(id: String): WorkoutPlanDto? =
        dao.getById(id)

    override suspend fun deleteById(id: String) {
        dao.deleteById(id)
    }
}