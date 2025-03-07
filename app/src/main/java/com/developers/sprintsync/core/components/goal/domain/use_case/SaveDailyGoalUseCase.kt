package com.developers.sprintsync.core.components.goal.domain.use_case

import com.developers.sprintsync.core.components.goal.data.data_source.DailyGoalDataSource
import com.developers.sprintsync.core.components.goal.data.model.DailyGoal
import javax.inject.Inject

class SaveDailyGoalUseCase
    @Inject
    constructor(
        private val repository: DailyGoalDataSource,
    ) {
        suspend operator fun invoke(dailyGoal: DailyGoal) = repository.saveDailyGoal(dailyGoal)
    }
