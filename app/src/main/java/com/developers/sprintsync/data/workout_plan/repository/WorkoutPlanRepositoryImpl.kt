package com.developers.sprintsync.data.workout_plan.repository

import com.developers.sprintsync.core.id_generator.IdGenerator
import com.developers.sprintsync.data.core.fromLocalOrRemote
import com.developers.sprintsync.data.core.runResult
import com.developers.sprintsync.data.core.toResource
import com.developers.sprintsync.data.workout_plan.data_source.local.LocalWorkoutPlanDataSource
import com.developers.sprintsync.data.workout_plan.data_source.remote.RemoteWorkoutPlanDataSource
import com.developers.sprintsync.data.workout_plan.mapper.toDomain
import com.developers.sprintsync.data.workout_plan.mapper.toDto
import com.developers.sprintsync.data.workout_plan.mapper.withId
import com.developers.sprintsync.domain.core.Resource
import com.developers.sprintsync.domain.core.ResourceList
import com.developers.sprintsync.domain.workouts_plan.model.WorkoutPlan
import com.developers.sprintsync.domain.workouts_plan.repository.WorkoutPlanRepository
import kotlinx.coroutines.flow.Flow

/*
    fun getWorkoutPlans(forceRefresh: Boolean): Flow<Resource<List<WorkoutPlan>>> = flow {
        emit(Resource.Loading)
        try {
            val cached = local.getAll()
            emit(Resource.Success(cached))
            if (forceRefresh || cached.isEmpty()) {
                val remote = remote.getAllSystemPlans()
                local.saveAll(remote)
                emit(Resource.Success(local.getAll()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
 */

class WorkoutPlanRepositoryImpl(
    private val local: LocalWorkoutPlanDataSource,
    private val remote: RemoteWorkoutPlanDataSource,
    private val idGenerator: IdGenerator,
) : WorkoutPlanRepository {

    override suspend fun save(plan: WorkoutPlan): Resource.Result<Unit> = runResult {
        val dto = plan.toDtoWithId()
        local.insert(dto)
        remote.save(dto)
    }

    override suspend fun saveAll(plans: List<WorkoutPlan>): Resource.Result<Unit> = runResult {
        val dtos = plans.toDtoWithId()
        local.insertAll(dtos)
        remote.saveAll(dtos)
    }

    override fun getAllSystemPlans(forceRefresh: Boolean): Flow<ResourceList<WorkoutPlan>> = fromLocalOrRemote(
        forceRefresh,
        readLocalSource = { local.getAllSystemPlansOnce() },
        writeLocalSource = { local.insertAll(it) },
        readRemoteSource = { remote.getAllSystemPlans() }).toDomain().toResource()


    override fun getAllByOwner(owner: String, forceRefresh: Boolean): Flow<ResourceList<WorkoutPlan>> =
        fromLocalOrRemote(
            forceRefresh,
            readLocalSource = { local.getAllByOwnerOnce(owner) },
            writeLocalSource = { local.insertAll(it) },
            readRemoteSource = { remote.getAllOwnerPlans(owner) }).toDomain().toResource()

    override suspend fun getById(id: String): Resource.Result<WorkoutPlan> = runResult {
        fromLocalOrRemote(
            readLocalSource = { local.getById(id) },
            writeLocalSource = { local.insert(it) },
            readRemoteSource = { remote.getById(id) }
        )?.toDomain()
    }

    override suspend fun deleteById(id: String): Resource.Result<Unit> = runResult {
        local.deleteById(id)
        remote.deleteById(id)
    }

    private fun WorkoutPlan.toDtoWithId() = toDto().withId(idGenerator.generate())
    private fun List<WorkoutPlan>.toDtoWithId() = this.map { it.toDtoWithId() }
}
