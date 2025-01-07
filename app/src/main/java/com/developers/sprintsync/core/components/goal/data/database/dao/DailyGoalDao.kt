package com.developers.sprintsync.core.components.goal.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.developers.sprintsync.core.components.goal.data.database.dto.DailyGoalEntity
import com.developers.sprintsync.core.components.goal.data.model.DailyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    @Insert
    suspend fun insetDailyGoal(dailyGoal: DailyGoalEntity)

    @Query("SELECT * FROM DailyGoalEntity ORDER BY id DESC")
    fun getAllDailyGoals(): Flow<List<DailyGoal>>
}
