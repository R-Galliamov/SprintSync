package com.developers.sprintsync.statistics.domain.ui

import com.developers.sprintsync.statistics.domain.chart.data.Metric

data class FormattedDailyGoal(
    val metric: Metric,
    val value: String,
)
