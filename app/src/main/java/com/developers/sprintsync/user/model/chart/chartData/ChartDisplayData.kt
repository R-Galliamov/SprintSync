package com.developers.sprintsync.user.model.chart.chartData

import com.developers.sprintsync.user.model.chart.navigator.RangePosition

data class ChartDisplayData(
    val data: Map<Int, DailyValues>,
    val rangePosition: RangePosition,
) {
    companion object {
        val EMPTY = ChartDisplayData(data = emptyMap(), rangePosition = RangePosition.NOT_INITIALIZED)
    }
}
