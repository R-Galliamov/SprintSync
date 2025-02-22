package com.developers.sprintsync.core.components.goal.data.repository

import com.developers.sprintsync.core.util.test.TestTimestamps
import com.developers.sprintsync.core.components.goal.data.model.DailyGoal
import com.developers.sprintsync.core.components.goal.data.model.Metric
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class TestDailyGoalRepository
    @Inject
    constructor() : DailyGoalRepository {
        private val tt = TestTimestamps

        private val goals: List<DailyGoal> =
            listOf(
                DailyGoal(0, tt.mondayTimestamp1, Metric.DISTANCE, 10f),
                // DailyGoal(0, tt.mondayTimestamp1, Metric.DURATION, 8000000f),
                // DailyGoal(0, tt.mondayTimestamp1, Metric.CALORIES, 100000f),
                // DailyGoal(0, tt.mondayTimestamp1 + 1, Metric.DISTANCE, 2000f),
                // DailyGoal(0, tt.wednesdayTimestamp1, Metric.DISTANCE, 2000f),
                // DailyGoal(0, tt.mondayTimestamp2, Metric.DISTANCE, 3000f),
                // DailyGoal(0, tt.mondayTimestamp3, Metric.DISTANCE, 4000f),
            )

        private val _dailyGoals = MutableStateFlow(goals)

        override val dailyGoals: Flow<List<DailyGoal>> = _dailyGoals

        override suspend fun saveDailyGoal(dailyGoal: DailyGoal) {
            _dailyGoals.update {
                it + dailyGoal
            }
        }
    }
