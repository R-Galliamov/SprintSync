package com.developers.sprintsync.domain.goal.use_case

import com.developers.sprintsync.data.goal.data_source.DailyGoalDataSource
import javax.inject.Inject

class GetDailyGoalsFlowUseCase
    @Inject
    constructor(
        repository: DailyGoalDataSource, // TODO create repository and fix all use cases
    ) {
        val dailyGoals = repository.dailyGoals
    }
