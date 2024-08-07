package com.developers.sprintsync.user.ui.userProfile.chart.data

import com.developers.sprintsync.user.model.chart.configuration.ChartConfiguration
import com.developers.sprintsync.user.ui.userProfile.chart.appearance.valueFormatter.ChartWeekDayFormatter
import com.developers.sprintsync.user.ui.userProfile.chart.appearance.valueFormatter.SelectiveYAxisValueFormatter
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.listener.ChartGestureListener

/**
 * A factory class for creating [ChartConfiguration] instances.
 */
class ChartConfigurationFactory {
    /**
     * Creates a [ChartConfiguration] instance with the provided parameters.
     *
     * @param displayedYValue The value to be displayed on the Y-axis.
     * @param referencedTimestamp A timestamp used as a starting value for formatting the X-axis labels.
     * @param onGestureListener A listener for handling chart gestures.
     * @return A configured [ChartConfiguration] object.
     */
    fun createConfiguration(
        displayedYValue: Float,
        referencedTimestamp: Long,
        onGestureListener: ChartGestureListener,
    ): ChartConfiguration {
        val yValueFormatter = SelectiveYAxisValueFormatter(displayedYValue)
        val xValueFormatter = ChartWeekDayFormatter(referencedTimestamp)
        return ChartConfiguration(yValueFormatter, xValueFormatter, onGestureListener)
    }
}
