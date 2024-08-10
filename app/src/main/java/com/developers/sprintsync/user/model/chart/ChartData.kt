package com.developers.sprintsync.user.model.chart

data class ChartData(
    val label: String,
    val referenceTimestamp: Long,
    val dailyPoints: List<DailyDataPoint>,
)
