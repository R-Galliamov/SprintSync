package com.developers.sprintsync.core.components.goal.domain.use_case

import com.developers.sprintsync.core.components.goal.data.repository.DailyGoalRepository
import javax.inject.Inject

class GetDailyGoalsFlowUseCase
    @Inject
    constructor(
        repository: DailyGoalRepository,
    ) {
        val dailyGoals = repository.dailyGoals
    }
