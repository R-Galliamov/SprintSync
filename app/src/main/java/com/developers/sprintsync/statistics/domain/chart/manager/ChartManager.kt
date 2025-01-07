package com.developers.sprintsync.statistics.domain.chart.manager

import com.developers.sprintsync.statistics.domain.chart.config.ChartConfigurationType
import com.developers.sprintsync.statistics.domain.chart.navigator.ChartNavigator
import com.developers.sprintsync.statistics.domain.chart.data.DisplayData
import com.developers.sprintsync.statistics.domain.chart.data.DailyValues
import com.developers.sprintsync.core.components.goal.data.model.Metric
import kotlinx.coroutines.flow.StateFlow

abstract class ChartManager {
    abstract val displayData: StateFlow<DisplayData>

    abstract val navigator: ChartNavigator

    abstract fun presetChartConfiguration(configType: ChartConfigurationType)

    abstract fun displayData(
        metric: Metric,
        data: List<DailyValues>,
        referencedTimestamp: Long,
    )

    abstract fun displayEntry(dayIndex: Int)

    fun displayRange(rangeIndex: Int) = navigator.setDisplayedRange(rangeIndex)

    fun navigateRange(direction: ChartNavigator.NavigationDirection) = navigator.shiftViewPortRange(direction)
}
