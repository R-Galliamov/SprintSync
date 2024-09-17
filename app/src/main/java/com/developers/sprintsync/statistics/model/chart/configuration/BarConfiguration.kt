package com.developers.sprintsync.statistics.model.chart.configuration

import com.github.mikephil.charting.formatter.ValueFormatter

data class BarConfiguration(
    val barColor: Int?,
    val barWidth: Float?,
    val missingBarColor: Int?,
    val missingBarHeight: Float,
    val barLabelColor: Int?,
    val barLabelSizeDp: Float?,
    val balLabelTypeFace: android.graphics.Typeface?,
    val valueFormatter: ValueFormatter? = null,
) {
    companion object {
        val EMPTY_CONFIGURATION =
            BarConfiguration(
                barColor = null,
                barWidth = null,
                missingBarColor = null,
                missingBarHeight = 0f,
                barLabelColor = null,
                barLabelSizeDp = null,
                balLabelTypeFace = null,
            )
    }
}
