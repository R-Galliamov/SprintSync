package com.developers.sprintsync.presentation.workouts_statistics.chart.config

import com.developers.sprintsync.presentation.workouts_statistics.chart.formatters.axis.SelectiveYAxisValueFormatter
import com.developers.sprintsync.presentation.workouts_statistics.chart.axis.YAxisScaler
import com.developers.sprintsync.presentation.workouts_statistics.chart.interaction.ChartGestureListener
import com.github.mikephil.charting.formatter.ValueFormatter

data class ChartConfiguration(
    val visibleXRange: Int,
    val yValueFormatter: SelectiveYAxisValueFormatter,
    val xValueFormatter: ValueFormatter,
    val yAxisScaler: YAxisScaler,
    val onGestureListener: ChartGestureListener,
)
