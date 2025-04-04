package com.developers.sprintsync.presentation.workouts_statistics.chart.data

import com.developers.sprintsync.presentation.workouts_statistics.chart.navigator.RangePosition

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
