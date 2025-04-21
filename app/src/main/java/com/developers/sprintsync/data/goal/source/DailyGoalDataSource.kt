package com.developers.sprintsync.data.goal.source

import com.developers.sprintsync.domain.goal.model.DailyGoal
import kotlinx.coroutines.flow.Flow

interface DailyGoalDataSource {
    fun getDailyGoalsFlow(): Flow<List<DailyGoal>>

    suspend fun saveDailyGoal(dailyGoal: DailyGoal)
}
