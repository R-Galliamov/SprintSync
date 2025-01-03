package com.developers.sprintsync.statistics.data.repository.dailyGoal

import com.developers.sprintsync.statistics.data.db.dao.DailyGoalDao
import com.developers.sprintsync.statistics.data.db.dto.DailyGoalEntity
import com.developers.sprintsync.statistics.domain.goal.DailyGoal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyGoalRepositoryImpl
    @Inject
    constructor(
        private val dao: DailyGoalDao,
    ) : DailyGoalRepository {
        override val dailyGoals = dao.getAllDailyGoals()

        override suspend fun saveDailyGoal(dailyGoal: DailyGoal) {
            val entity = DailyGoalEntity.fromDto(dailyGoal)
            dao.insetDailyGoal(entity)
        }
    }
