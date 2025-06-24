package com.developers.sprintsync.domain.workouts_plan.use_case

import com.developers.sprintsync.data.goal.repository.DailyGoalRepository
import com.developers.sprintsync.domain.workouts_plan.model.DailyGoal
import javax.inject.Inject

class SaveDailyGoalUseCase
    @Inject
    constructor(
        private val repository: DailyGoalRepository,
    ) {
        suspend operator fun invoke(dailyGoal: DailyGoal) = repository.saveDailyGoal(dailyGoal)
    }
