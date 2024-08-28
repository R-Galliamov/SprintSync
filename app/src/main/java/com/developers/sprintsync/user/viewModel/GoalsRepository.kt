package com.developers.sprintsync.user.viewModel

import com.developers.sprintsync.tracking.dataStorage.repository.track.TestRepository
import com.developers.sprintsync.user.model.DailyGoal
import com.developers.sprintsync.user.model.chart.chartData.Metric
import javax.inject.Inject

class GoalsRepository
    @Inject
    constructor(
        testRepository: TestRepository,
    ) {
        val goals =
            listOf(
                DailyGoal(testRepository.mondayTimestamp1, Metric.DISTANCE, 1000f),
                DailyGoal(testRepository.mondayTimestamp1, Metric.DURATION, 8000000f),
                DailyGoal(testRepository.mondayTimestamp1, Metric.CALORIES, 100000f),
                DailyGoal(testRepository.mondayTimestamp1 + 1, Metric.DISTANCE, 2000f),
                DailyGoal(testRepository.wednesdayTimestamp1, Metric.DISTANCE, 2000f),
                DailyGoal(testRepository.mondayTimestamp2, Metric.DISTANCE, 3000f),
                DailyGoal(testRepository.mondayTimestamp3, Metric.DISTANCE, 4000f),
            )
    }
