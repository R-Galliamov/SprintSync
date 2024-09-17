package com.developers.sprintsync.statistics.util.creater

import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.statistics.model.goal.DailyGoal

object DailyGoalCreator {
    fun create(
        metric: Metric,
        value: Float,
    ) = DailyGoal(DEFAULT_DAILY_GOAL_ID, System.currentTimeMillis(), metric, value)

    private const val DEFAULT_DAILY_GOAL_ID = 0
}
