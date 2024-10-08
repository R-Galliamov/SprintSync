package com.developers.sprintsync.statistics.dataStorage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.developers.sprintsync.statistics.dataStorage.db.dto.DailyGoalEntity
import com.developers.sprintsync.statistics.model.goal.DailyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    @Insert
    suspend fun insetDailyGoal(dailyGoal: DailyGoalEntity)

    @Query("SELECT * FROM DailyGoalEntity ORDER BY id DESC")
    fun getAllDailyGoals(): Flow<List<DailyGoal>>
}
