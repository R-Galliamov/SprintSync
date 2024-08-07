package com.developers.sprintsync.user.model.chart.configuration

import com.developers.sprintsync.user.ui.userProfile.chart.interaction.listener.ChartGestureListener
import com.github.mikephil.charting.formatter.ValueFormatter

data class ChartConfiguration(
    val yValueFormatter: ValueFormatter,
    val xValueFormatter: ValueFormatter,
    val onGestureListener: ChartGestureListener,
)
