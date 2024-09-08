package com.developers.sprintsync.user.dataStorage.repository.dailyGoal

import com.developers.sprintsync.user.model.DailyGoal
import kotlinx.coroutines.flow.Flow

interface DailyGoalRepository {
    val dailyGoals: Flow<List<DailyGoal>>

    suspend fun saveDailyGoal(dailyGoal: DailyGoal)
}
