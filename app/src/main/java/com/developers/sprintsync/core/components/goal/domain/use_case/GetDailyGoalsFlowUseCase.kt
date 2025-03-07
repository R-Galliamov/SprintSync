package com.developers.sprintsync.core.components.goal.domain.use_case

import com.developers.sprintsync.core.components.goal.data.data_source.DailyGoalDataSource
import javax.inject.Inject

class GetDailyGoalsFlowUseCase
    @Inject
    constructor(
        repository: DailyGoalDataSource,
    ) {
        val dailyGoals = repository.dailyGoals
    }
