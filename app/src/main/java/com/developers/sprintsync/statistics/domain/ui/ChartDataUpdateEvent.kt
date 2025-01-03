package com.developers.sprintsync.statistics.domain.ui

import com.developers.sprintsync.statistics.domain.chart.data.DailyValues
import com.developers.sprintsync.statistics.domain.chart.data.Metric

data class ChartDataUpdateEvent(
    val metric: Metric,
    val dailyValues: List<DailyValues>,
    val referenceTimestamp: Long,
)
