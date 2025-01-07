package com.developers.sprintsync.core.components.goal.data.repository

import com.developers.sprintsync.core.components.goal.data.model.DailyGoal
import kotlinx.coroutines.flow.Flow

interface DailyGoalRepository {
    val dailyGoals: Flow<List<DailyGoal>>

    suspend fun saveDailyGoal(dailyGoal: DailyGoal)
}
