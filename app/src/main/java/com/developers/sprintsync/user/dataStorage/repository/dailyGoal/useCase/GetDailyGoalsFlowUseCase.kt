package com.developers.sprintsync.user.dataStorage.repository.dailyGoal.useCase

import com.developers.sprintsync.user.dataStorage.repository.dailyGoal.DailyGoalRepository
import javax.inject.Inject

class GetDailyGoalsFlowUseCase
    @Inject
    constructor(
        repository: DailyGoalRepository,
    ) {
        val dailyGoals = repository.dailyGoals
    }
