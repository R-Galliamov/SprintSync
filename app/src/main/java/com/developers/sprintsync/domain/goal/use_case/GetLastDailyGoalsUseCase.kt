package com.developers.sprintsync.domain.goal.use_case

import com.developers.sprintsync.data.goal.repository.DailyGoalRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLastDailyGoalsUseCase
    @Inject
    constructor(
        private val repository: DailyGoalRepository
    ) {
        operator fun invoke() =
            repository.dailyGoals.map { list ->
                if (list.isEmpty()) return@map emptyMap()
                list.groupBy { it.metricType }.mapValues { goal -> goal.value.maxBy { it.timestamp } }
            }
    }
