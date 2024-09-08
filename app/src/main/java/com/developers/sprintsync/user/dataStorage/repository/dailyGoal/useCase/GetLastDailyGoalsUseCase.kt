package com.developers.sprintsync.user.dataStorage.repository.dailyGoal.useCase

import com.developers.sprintsync.user.dataStorage.repository.dailyGoal.DailyGoalRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLastDailyGoalsUseCase
    @Inject
    constructor(
        private val repository: DailyGoalRepository,
    ) {
        operator fun invoke() =
            repository.dailyGoals.map { list ->
                if (list.isEmpty()) return@map emptyMap()
                list.groupBy { it.metricType }.mapValues { goal -> goal.value.maxBy { it.timestamp } }
            }
    }
