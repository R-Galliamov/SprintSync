package com.developers.sprintsync.user.model.chart.configuration

data class BarConfiguration(
    val barColor: Int?,
    val barWidth: Float?,
    val missingBarColor: Int?,
    val missingBarHeight: Float,
    val label: String?,
) {
    companion object {
        val EMPTY_CONFIGURATION =
            BarConfiguration(
                barColor = null,
                barWidth = null,
                missingBarColor = null,
                missingBarHeight = 0f,
                label = null,
            )
    }
}
