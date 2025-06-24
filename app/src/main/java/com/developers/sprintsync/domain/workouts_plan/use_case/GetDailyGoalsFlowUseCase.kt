package com.developers.sprintsync.domain.workouts_plan.use_case

import com.developers.sprintsync.data.goal.repository.DailyGoalRepository
import javax.inject.Inject

class GetDailyGoalsFlowUseCase
    @Inject
    constructor(
        repository: DailyGoalRepository
    ) {
        val dailyGoals = repository.dailyGoals
    }
