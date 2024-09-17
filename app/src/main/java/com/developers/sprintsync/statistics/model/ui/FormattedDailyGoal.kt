package com.developers.sprintsync.statistics.model.ui

import com.developers.sprintsync.statistics.model.chart.chartData.Metric

data class FormattedDailyGoal(
    val metric: Metric,
    val value: String,
)
