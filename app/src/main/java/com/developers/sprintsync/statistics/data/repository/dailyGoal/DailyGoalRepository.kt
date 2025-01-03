package com.developers.sprintsync.statistics.data.repository.dailyGoal

import com.developers.sprintsync.statistics.domain.goal.DailyGoal
import kotlinx.coroutines.flow.Flow

interface DailyGoalRepository {
    val dailyGoals: Flow<List<DailyGoal>>

    suspend fun saveDailyGoal(dailyGoal: DailyGoal)
}
