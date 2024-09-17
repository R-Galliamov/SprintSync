package com.developers.sprintsync.statistics.ui.statistics.chart.interaction.manager

import com.developers.sprintsync.statistics.model.chart.chartData.ChartDisplayData
import com.developers.sprintsync.statistics.model.chart.chartData.DailyValues
import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.statistics.ui.statistics.chart.configuration.ChartConfigurationType
import com.developers.sprintsync.statistics.ui.statistics.chart.interaction.navigation.ChartNavigator
import kotlinx.coroutines.flow.StateFlow

abstract class ChartManager {
    abstract val displayData: StateFlow<ChartDisplayData>

    abstract val navigator: ChartNavigator

    abstract fun presetChartConfiguration(configType: ChartConfigurationType)

    abstract fun displayData(
        metric: Metric,
        data: List<DailyValues>,
        referencedTimestamp: Long,
    )

    abstract fun displayEntry(dayIndex: Int)

    fun displayRange(rangeIndex: Int) = navigator.displayRange(rangeIndex)

    fun navigateRange(direction: ChartNavigator.NavigationDirection) = navigator.navigateRange(direction)
}
