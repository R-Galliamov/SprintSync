package com.developers.sprintsync.user.dataStorage.repository.dailyGoal.useCase

import com.developers.sprintsync.user.dataStorage.repository.dailyGoal.DailyGoalRepository
import com.developers.sprintsync.user.model.DailyGoal
import javax.inject.Inject

class SaveDailyGoalUseCase
    @Inject
    constructor(
        private val repository: DailyGoalRepository,
    ) {
        suspend operator fun invoke(dailyGoal: DailyGoal) = repository.saveDailyGoal(dailyGoal)
    }
