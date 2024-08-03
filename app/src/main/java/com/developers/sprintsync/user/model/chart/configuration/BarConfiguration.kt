package com.developers.sprintsync.user.model.chart.configuration

data class BarConfiguration(
    val barColor: Int?,
    val barWidth: Float?,
    val missingBarColor: Int?,
    val missingBarHeight: Float,
    val barLabelColor: Int?,
    val barLabelSizeDp: Float?,
    val balLabelTypeFace: android.graphics.Typeface?,
    val dataLabel: String?,
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
                dataLabel = null,
            )
    }
}
