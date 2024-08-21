package com.developers.sprintsync.user.model.chart.chartData

data class ChartDisplayData(
    val metric: Metric,
    val data: List<DailyValues>,
)
