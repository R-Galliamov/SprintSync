package com.developers.sprintsync.statistics.data.repository.dailyGoal.useCase

import com.developers.sprintsync.statistics.data.repository.dailyGoal.DailyGoalRepository
import javax.inject.Inject

class GetDailyGoalsFlowUseCase
    @Inject
    constructor(
        repository: DailyGoalRepository,
    ) {
        val dailyGoals = repository.dailyGoals
    }
