package com.developers.sprintsync.statistics.presentation.model

import com.developers.sprintsync.core.components.goal.data.model.Metric

data class FormattedDailyGoal(
    val metric: Metric,
    val value: String,
)
