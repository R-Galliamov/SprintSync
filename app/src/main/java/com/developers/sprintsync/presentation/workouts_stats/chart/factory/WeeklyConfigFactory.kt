package com.developers.sprintsync.presentation.workouts_stats.chart.factory

import com.developers.sprintsync.presentation.workouts_stats.chart.config.ChartConfiguration
import com.developers.sprintsync.presentation.workouts_stats.chart.formatters.axis.WeekdayAxisFormatter
import com.developers.sprintsync.presentation.workouts_stats.chart.formatters.axis.DefaultSelectiveYAxisValueFormatter
import com.developers.sprintsync.presentation.workouts_stats.chart.axis.YAxisScaler
import com.developers.sprintsync.presentation.workouts_stats.chart.interaction.ChartGestureListener

class WeeklyConfigFactory {
    fun createConfiguration(
        referencedTimestamp: Long,
        onGestureListener: ChartGestureListener,
    ): ChartConfiguration {
        val yValueFormatter = DefaultSelectiveYAxisValueFormatter()
        val xValueFormatter = WeekdayAxisFormatter(referencedTimestamp)
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
