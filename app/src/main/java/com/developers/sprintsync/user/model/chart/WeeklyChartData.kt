package com.developers.sprintsync.user.model.chart

data class WeeklyChartData(
    val label: String,
    val referenceTimestamp: Long,
    val data: List<DailyDataPoint>,
)
