package com.developers.sprintsync.user.ui.userProfile.chart.data

import com.developers.sprintsync.user.model.chart.DailyDataPoint

class ChartDataSelector {
    fun getSubList(
        data: List<DailyDataPoint>,
        startIndex: Int,
        endIndex: Int,
    ): List<DailyDataPoint> = data.subList(startIndex, endIndex)
}
