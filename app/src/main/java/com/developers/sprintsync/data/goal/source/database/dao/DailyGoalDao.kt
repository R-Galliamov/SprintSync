package com.developers.sprintsync.data.goal.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.developers.sprintsync.data.goal.source.database.dto.DailyGoalEntity
import com.developers.sprintsync.domain.workouts_plan.model.DailyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    @Insert
    suspend fun insetDailyGoal(dailyGoal: DailyGoalEntity)

    @Query("SELECT * FROM DailyGoalEntity ORDER BY id DESC")
    fun getAllDailyGoals(): Flow<List<DailyGoal>>
}
