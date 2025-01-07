package com.developers.sprintsync.statistics.presentation.event

import com.developers.sprintsync.statistics.domain.chart.data.DailyValues
import com.developers.sprintsync.core.components.goal.data.model.Metric

data class ChartDataUpdateEvent(
    val metric: Metric,
    val dailyValues: List<DailyValues>,
    val referenceTimestamp: Long,
)
