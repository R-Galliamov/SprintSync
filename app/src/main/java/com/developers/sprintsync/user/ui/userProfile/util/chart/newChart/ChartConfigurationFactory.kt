package com.developers.sprintsync.user.ui.userProfile.util.chart.newChart

import com.developers.sprintsync.user.model.chart.configuration.ChartConfiguration
import com.developers.sprintsync.user.ui.userProfile.util.chart.listener.ChartGestureListener
import com.developers.sprintsync.user.ui.userProfile.util.chart.styling.valueFormatter.ChartDayFormatter
import com.developers.sprintsync.user.ui.userProfile.util.chart.styling.valueFormatter.SelectiveYAxisValueFormatter

class ChartConfigurationFactory {
    fun createConfiguration(
        displayedYValue: Float,
        referencedTimestamp: Long,
        onGestureListener: ChartGestureListener,
    ): ChartConfiguration {
        val yValueFormatter = SelectiveYAxisValueFormatter(displayedYValue)
        val xValueFormatter = ChartDayFormatter(referencedTimestamp)
        return ChartConfiguration(yValueFormatter, xValueFormatter, onGestureListener)
    }
}
