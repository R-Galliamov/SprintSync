package com.developers.sprintsync.presentation.workouts_statistics.chart.config

import com.github.mikephil.charting.data.LineDataSet

data class LineConfiguration(
    val lineColor: Int?,
    val lineWidth: Float?,
    val drawValues: Boolean?,
    val drawCircles: Boolean?,
    val drawFilled: Boolean?,
    val mode: LineDataSet.Mode?,
) {
    companion object {
        val EMPTY_CONFIGURATION =
            LineConfiguration(
                lineColor = null,
                lineWidth = null,
                drawValues = null,
                drawCircles = null,
                drawFilled = null,
                mode = null,
            )
    }
}
