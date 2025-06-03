package com.developers.sprintsync.presentation.workouts_stats.chart.data

import com.developers.sprintsync.presentation.workouts_stats.chart.navigator.RangePosition

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
