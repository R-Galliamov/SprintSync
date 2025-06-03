package com.developers.sprintsync.presentation.workouts_stats.chart.manager

import com.developers.sprintsync.presentation.workouts_stats.chart.config.ChartConfigurationType
import com.developers.sprintsync.presentation.workouts_stats.chart.navigator.ChartNavigator
import com.developers.sprintsync.presentation.workouts_stats.chart.data.DisplayData
import com.developers.sprintsync.presentation.workouts_stats.chart.data.DailyValues
import com.developers.sprintsync.domain.goal.model.Metric
import kotlinx.coroutines.flow.StateFlow

abstract class ChartManager {
    abstract val displayData: StateFlow<DisplayData>

    abstract val navigator: ChartNavigator

    abstract fun presetChart(configType: ChartConfigurationType)

    abstract fun displayData(
        metric: Metric,
        data: List<DailyValues>,
        referencedTimestamp: Long,
    )

    abstract fun displayEntry(dayIndex: Int)

    fun displayRange(rangeIndex: Int) = navigator.setDisplayedRange(rangeIndex)

    fun navigateRange(direction: ChartNavigator.NavigationDirection) = navigator.shiftViewPortRange(direction)
}
