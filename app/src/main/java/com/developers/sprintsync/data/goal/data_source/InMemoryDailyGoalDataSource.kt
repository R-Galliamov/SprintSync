package com.developers.sprintsync.data.goal.data_source

import com.developers.sprintsync.data.goal.database.dao.DailyGoalDao
import com.developers.sprintsync.data.goal.database.dto.DailyGoalEntity
import com.developers.sprintsync.domain.goal.model.DailyGoal
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
