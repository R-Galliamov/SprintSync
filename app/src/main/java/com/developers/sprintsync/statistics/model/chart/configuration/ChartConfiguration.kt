package com.developers.sprintsync.statistics.model.chart.configuration

import com.developers.sprintsync.statistics.ui.statistics.chart.configuration.valueFormatter.axis.SelectiveYAxisValueFormatter
import com.developers.sprintsync.statistics.ui.statistics.chart.interaction.animation.YAxisScaler
import com.developers.sprintsync.statistics.ui.statistics.chart.interaction.listener.ChartGestureListener
import com.github.mikephil.charting.formatter.ValueFormatter

data class ChartConfiguration(
    val visibleXRange: Int,
    val yValueFormatter: SelectiveYAxisValueFormatter,
    val xValueFormatter: ValueFormatter,
    val yAxisScaler: YAxisScaler,
    val onGestureListener: ChartGestureListener,
)
