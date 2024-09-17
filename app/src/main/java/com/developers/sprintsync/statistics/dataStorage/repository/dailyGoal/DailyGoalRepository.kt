package com.developers.sprintsync.statistics.dataStorage.repository.dailyGoal

import com.developers.sprintsync.statistics.model.goal.DailyGoal
import kotlinx.coroutines.flow.Flow

interface DailyGoalRepository {
    val dailyGoals: Flow<List<DailyGoal>>

    suspend fun saveDailyGoal(dailyGoal: DailyGoal)
}
