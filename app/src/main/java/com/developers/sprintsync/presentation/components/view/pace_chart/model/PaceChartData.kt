package com.developers.sprintsync.presentation.components.view.pace_chart.model

import com.github.mikephil.charting.data.Entry

data class PaceChartData(
    val data: List<List<Entry>>,
    val maxPace: Float,
    val minPace: Float,
) {
    companion object {
        val EMPTY = PaceChartData(emptyList(), 0f, 0f)
    }
}
