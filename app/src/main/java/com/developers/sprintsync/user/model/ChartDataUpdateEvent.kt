package com.developers.sprintsync.user.model

import com.developers.sprintsync.user.model.chart.chartData.DailyValues
import com.developers.sprintsync.user.model.chart.chartData.Metric

data class ChartDataUpdateEvent(
    val metric: Metric,
    val dailyValues: List<DailyValues>,
    val referenceTimestamp: Long,
)
