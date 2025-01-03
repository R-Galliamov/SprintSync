package com.developers.sprintsync.statistics.domain.chart.config

import com.developers.sprintsync.statistics.domain.chart.formatters.axis.SelectiveYAxisValueFormatter
import com.developers.sprintsync.statistics.domain.chart.axis.YAxisScaler
import com.developers.sprintsync.statistics.domain.chart.interaction.ChartGestureListener
import com.github.mikephil.charting.formatter.ValueFormatter

data class ChartConfiguration(
    val visibleXRange: Int,
    val yValueFormatter: SelectiveYAxisValueFormatter,
    val xValueFormatter: ValueFormatter,
    val yAxisScaler: YAxisScaler,
    val onGestureListener: ChartGestureListener,
)
