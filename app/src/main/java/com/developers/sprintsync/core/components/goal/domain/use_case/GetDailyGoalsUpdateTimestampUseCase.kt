package com.developers.sprintsync.core.components.goal.domain.use_case

import com.developers.sprintsync.core.components.goal.data.repository.DailyGoalRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDailyGoalsUpdateTimestampUseCase
    @Inject
    constructor(
        private val repository: DailyGoalRepository,
    ) {
        operator fun invoke() = repository.dailyGoals.map { dailyGoals -> dailyGoals.maxOfOrNull { it.timestamp } }
    }
