package com.developers.sprintsync.data.goal.data_source

import com.developers.sprintsync.domain.goal.model.DailyGoal
import kotlinx.coroutines.flow.Flow

interface DailyGoalDataSource {
    val dailyGoals: Flow<List<DailyGoal>>

    suspend fun saveDailyGoal(dailyGoal: DailyGoal)
}
