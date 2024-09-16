package com.developers.sprintsync.user.util.creater

import com.developers.sprintsync.user.model.goal.DailyGoal
import com.developers.sprintsync.user.model.chart.chartData.Metric

object DailyGoalCreator {
    fun create(
        metric: Metric,
        value: Float,
    ) = DailyGoal(DEFAULT_DAILY_GOAL_ID, System.currentTimeMillis(), metric, value)

    private const val DEFAULT_DAILY_GOAL_ID = 0
}
