package com.developers.sprintsync.statistics.dataStorage.repository.dailyGoal.useCase

import com.developers.sprintsync.statistics.dataStorage.repository.dailyGoal.DailyGoalRepository
import com.developers.sprintsync.statistics.model.goal.DailyGoal
import javax.inject.Inject

class SaveDailyGoalUseCase
    @Inject
    constructor(
        private val repository: DailyGoalRepository,
    ) {
        suspend operator fun invoke(dailyGoal: DailyGoal) = repository.saveDailyGoal(dailyGoal)
    }
