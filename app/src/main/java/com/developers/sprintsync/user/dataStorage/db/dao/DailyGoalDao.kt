package com.developers.sprintsync.user.dataStorage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.developers.sprintsync.user.dataStorage.db.dto.DailyGoalEntity
import com.developers.sprintsync.user.model.DailyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    @Insert
    suspend fun insetDailyGoal(dailyGoal: DailyGoalEntity)

    @Query("SELECT * FROM DailyGoalEntity ORDER BY id DESC")
    fun getAllDailyGoals(): Flow<List<DailyGoal>>
}
