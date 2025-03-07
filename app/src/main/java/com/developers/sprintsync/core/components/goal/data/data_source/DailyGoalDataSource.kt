package com.developers.sprintsync.core.components.goal.data.data_source

import com.developers.sprintsync.core.components.goal.data.model.DailyGoal
import kotlinx.coroutines.flow.Flow

interface DailyGoalDataSource {
    val dailyGoals: Flow<List<DailyGoal>>

    suspend fun saveDailyGoal(dailyGoal: DailyGoal)
}
