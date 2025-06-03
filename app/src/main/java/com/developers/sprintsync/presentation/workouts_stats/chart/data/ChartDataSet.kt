package com.developers.sprintsync.presentation.workouts_stats.chart.data

data class ChartDataSet(
    val referenceTimestamp: Long,
    val data: MetricToDailyValues,
) {
    companion object {
        val EMPTY = ChartDataSet(0, emptyMap())
    }
}
