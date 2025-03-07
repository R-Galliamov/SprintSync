package com.developers.sprintsync.core.components.goal.data.data_source

import com.developers.sprintsync.core.components.goal.data.database.dao.DailyGoalDao
import com.developers.sprintsync.core.components.goal.data.database.dto.DailyGoalEntity
import com.developers.sprintsync.core.components.goal.data.model.DailyGoal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryDailyGoalDataSource
    @Inject
    constructor(
        private val dao: DailyGoalDao,
    ) : DailyGoalDataSource {
        override val dailyGoals = dao.getAllDailyGoals()

        override suspend fun saveDailyGoal(dailyGoal: DailyGoal) {
            val entity = DailyGoalEntity.fromDto(dailyGoal)
            dao.insetDailyGoal(entity)
        }
    }
