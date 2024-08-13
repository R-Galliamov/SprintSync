package com.developers.sprintsync.user.model.chart.chartData

data class ChartData(
    val label: String,
    val referenceTimestamp: Long,
    val dailyPoints: List<DailyDataPoint>,
)
