package com.developers.sprintsync.update_goals.presentation.util

import com.developers.sprintsync.core.components.goal.data.model.Metric
import com.developers.sprintsync.core.components.goal.data.model.DailyGoal

object DailyGoalCreator {
    fun create(
        metric: Metric,
        value: Float,
    ) = DailyGoal(DEFAULT_DAILY_GOAL_ID, System.currentTimeMillis(), metric, value)

    private const val DEFAULT_DAILY_GOAL_ID = 0
}
