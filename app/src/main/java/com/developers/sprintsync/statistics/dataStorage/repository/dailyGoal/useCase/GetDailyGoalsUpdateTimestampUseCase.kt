package com.developers.sprintsync.statistics.dataStorage.repository.dailyGoal.useCase

import com.developers.sprintsync.statistics.dataStorage.repository.dailyGoal.DailyGoalRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDailyGoalsUpdateTimestampUseCase
    @Inject
    constructor(
        private val repository: DailyGoalRepository,
    ) {
        operator fun invoke() = repository.dailyGoals.map { dailyGoals -> dailyGoals.maxOfOrNull { it.timestamp } }
    }
