package com.developers.sprintsync.user.dataStorage.repository.dailyGoal

import com.developers.sprintsync.user.dataStorage.db.dao.DailyGoalDao
import com.developers.sprintsync.user.dataStorage.db.dto.DailyGoalEntity
import com.developers.sprintsync.user.model.goal.DailyGoal
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
