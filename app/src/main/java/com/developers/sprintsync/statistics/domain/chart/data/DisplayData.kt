package com.developers.sprintsync.statistics.domain.chart.data

import com.developers.sprintsync.statistics.domain.chart.navigator.RangePosition

data class DisplayData(
    val data: Map<Int, DailyValues>,
    val rangePosition: RangePosition,
) {
    companion object {
        val EMPTY =
            DisplayData(
                data = emptyMap(),
                rangePosition = RangePosition.NOT_INITIALIZED,
            )
    }
}
