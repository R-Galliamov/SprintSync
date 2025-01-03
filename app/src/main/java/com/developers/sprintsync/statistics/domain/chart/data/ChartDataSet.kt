package com.developers.sprintsync.statistics.domain.chart.data

data class ChartDataSet(
    val referenceTimestamp: Long,
    val data: MetricToDailyValues,
) {
    companion object {
        val EMPTY = ChartDataSet(0, emptyMap())
    }
}
