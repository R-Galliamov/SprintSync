package com.developers.sprintsync.tracking.analytics.model

import com.github.mikephil.charting.data.Entry

data class PaceChartData(
    val data: List<List<Entry>>,
    val maxPace: Float,
    val minPace: Float,
)
