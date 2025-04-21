package com.developers.sprintsync.data.goal.source

import com.developers.sprintsync.data.goal.source.database.dao.DailyGoalDao
import com.developers.sprintsync.data.goal.source.database.dto.DailyGoalEntity
import com.developers.sprintsync.domain.goal.model.DailyGoal
import kotlinx.coroutines.flow.Flow

class LocalDailyGoalDataSource(
        private val dao: DailyGoalDao,
    ) : DailyGoalDataSource {
        override fun getDailyGoalsFlow(): Flow<List<DailyGoal>> = dao.getAllDailyGoals()

        override suspend fun saveDailyGoal(dailyGoal: DailyGoal) {
            val entity = DailyGoalEntity.fromDto(dailyGoal)
            dao.insetDailyGoal(entity)
        }
    }
