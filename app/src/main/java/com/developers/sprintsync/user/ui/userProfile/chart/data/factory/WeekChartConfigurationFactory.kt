package com.developers.sprintsync.user.ui.userProfile.chart.data.factory

import com.developers.sprintsync.user.model.chart.configuration.ChartConfiguration
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter.ChartWeekDayFormatter
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter.SelectiveYAxisValueFormatterImpl
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.animation.YAxisScaler
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.listener.ChartGestureListener

class WeekChartConfigurationFactory {
    fun createConfiguration(
        referencedTimestamp: Long,
        onGestureListener: ChartGestureListener,
    ): ChartConfiguration {
        val yValueFormatter = SelectiveYAxisValueFormatterImpl()
        val xValueFormatter = ChartWeekDayFormatter(referencedTimestamp)
        return ChartConfiguration(
            yValueFormatter = yValueFormatter,
            xValueFormatter = xValueFormatter,
            onGestureListener = onGestureListener,
            visibleXRange = DAYS_PER_WEEK,
            yAxisScaler = YAxisScaler(),
        )
    }

    companion object {
        private const val DAYS_PER_WEEK = 7
    }
}
