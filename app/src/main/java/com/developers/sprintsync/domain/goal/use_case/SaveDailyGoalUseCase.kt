package com.developers.sprintsync.domain.goal.use_case

import com.developers.sprintsync.data.goal.data_source.DailyGoalDataSource
import com.developers.sprintsync.domain.goal.model.DailyGoal
import javax.inject.Inject

class SaveDailyGoalUseCase
    @Inject
    constructor(
        private val repository: DailyGoalDataSource,
    ) {
        suspend operator fun invoke(dailyGoal: DailyGoal) = repository.saveDailyGoal(dailyGoal)
    }
