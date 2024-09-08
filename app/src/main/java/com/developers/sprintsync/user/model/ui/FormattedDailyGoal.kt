package com.developers.sprintsync.user.model.ui

import com.developers.sprintsync.user.model.chart.chartData.Metric

data class FormattedDailyGoal(
    val metric: Metric,
    val value: String,
)
