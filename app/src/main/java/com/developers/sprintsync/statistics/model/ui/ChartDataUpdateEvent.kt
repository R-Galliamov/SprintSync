package com.developers.sprintsync.statistics.model.ui

import com.developers.sprintsync.statistics.model.chart.chartData.DailyValues
import com.developers.sprintsync.statistics.model.chart.chartData.Metric

data class ChartDataUpdateEvent(
    val metric: Metric,
    val dailyValues: List<DailyValues>,
    val referenceTimestamp: Long,
)
