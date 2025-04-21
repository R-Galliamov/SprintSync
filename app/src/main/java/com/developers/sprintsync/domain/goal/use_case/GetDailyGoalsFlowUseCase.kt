package com.developers.sprintsync.domain.goal.use_case

import com.developers.sprintsync.data.goal.repository.DailyGoalRepository
import com.developers.sprintsync.data.goal.source.DailyGoalDataSource
import javax.inject.Inject

class GetDailyGoalsFlowUseCase
    @Inject
    constructor(
        repository: DailyGoalRepository
    ) {
        val dailyGoals = repository.dailyGoals
    }
