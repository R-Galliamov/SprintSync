package com.developers.sprintsync.data.workout_plan.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDto
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {

    @Upsert
    suspend fun insert(dto: WorkoutPlanDto)

    @Upsert
    suspend fun insertAll(dtos: List<WorkoutPlanDto>)

    @Query("SELECT * FROM workout_plan WHERE owner = :owner")
    fun getAllByOwner(owner: String): Flow<List<WorkoutPlanDto>>

    @Query("SELECT * FROM workout_plan WHERE owner = :owner")
    suspend fun getAllByOwnerOnce(owner: String): List<WorkoutPlanDto>

    @Query("SELECT * FROM workout_plan")
    suspend fun getAll(): List<WorkoutPlanDto>

    @Query("SELECT * FROM workout_plan")
    fun getAllFlow(): Flow<List<WorkoutPlanDto>>

    @Query("SELECT * FROM workout_plan WHERE :id = id")
    suspend fun getById(id: String): WorkoutPlanDto?

    @Query("DELETE FROM workout_plan WHERE id = :id")
    suspend fun deleteById(id: String)

    @Delete
    suspend fun delete(dto: WorkoutPlanDto)

}