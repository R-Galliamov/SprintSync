package com.developers.sprintsync.user.model.chart.configuration

import com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter.axis.SelectiveYAxisValueFormatter
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.animation.YAxisScaler
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.listener.ChartGestureListener
import com.github.mikephil.charting.formatter.ValueFormatter

data class ChartConfiguration(
    val visibleXRange: Int,
    val yValueFormatter: SelectiveYAxisValueFormatter,
    val xValueFormatter: ValueFormatter,
    val yAxisScaler: YAxisScaler,
    val onGestureListener: ChartGestureListener,
)
