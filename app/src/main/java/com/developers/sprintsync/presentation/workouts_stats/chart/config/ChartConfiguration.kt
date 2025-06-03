package com.developers.sprintsync.presentation.workouts_stats.chart.config

import com.developers.sprintsync.presentation.workouts_stats.chart.formatters.axis.SelectiveYAxisValueFormatter
import com.developers.sprintsync.presentation.workouts_stats.chart.axis.YAxisScaler
import com.developers.sprintsync.presentation.workouts_stats.chart.interaction.ChartGestureListener
import com.github.mikephil.charting.formatter.ValueFormatter

data class ChartConfiguration(
    val visibleXRange: Int,
    val yValueFormatter: SelectiveYAxisValueFormatter,
    val xValueFormatter: ValueFormatter,
    val yAxisScaler: YAxisScaler,
    val onGestureListener: ChartGestureListener,
)
