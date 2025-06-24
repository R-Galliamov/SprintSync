package com.developers.sprintsync.data.goal.repository

import com.developers.sprintsync.data.goal.source.DailyGoalDataSource
import com.developers.sprintsync.domain.workouts_plan.model.DailyGoal
import kotlinx.coroutines.flow.Flow

interface DailyGoalRepository {
    val dailyGoals: Flow<List<DailyGoal>>

    suspend fun saveDailyGoal(dailyGoal: DailyGoal)
}

class DailyGoalRepositoryImpl(
    private val localDataSource: DailyGoalDataSource,
) : DailyGoalRepository {
    override val dailyGoals: Flow<List<DailyGoal>> = localDataSource.getDailyGoalsFlow()

    override suspend fun saveDailyGoal(dailyGoal: DailyGoal) {
        localDataSource.saveDailyGoal(dailyGoal)
    }
}
