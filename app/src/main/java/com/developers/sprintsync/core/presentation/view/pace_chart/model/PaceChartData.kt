package com.developers.sprintsync.core.presentation.view.pace_chart.model

import com.github.mikephil.charting.data.Entry

data class PaceChartData(
    val data: List<List<Entry>>,
    val maxPace: Float,
    val minPace: Float,
)