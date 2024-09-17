package com.developers.sprintsync.statistics.model.chart.chartData

data class ChartDataSet(
    val referenceTimestamp: Long,
    val data: MetricToDailyValues,
) {
    companion object {
        val EMPTY = ChartDataSet(0, emptyMap())
    }
}
