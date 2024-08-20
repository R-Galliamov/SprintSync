package com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager

import com.developers.sprintsync.user.model.chart.chartData.IndexedDailyValues
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.ChartConfigurationType
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation.ChartNavigator
import kotlinx.coroutines.flow.MutableStateFlow

abstract class ChartManager {
    abstract val displayedData: MutableStateFlow<IndexedDailyValues>

    abstract val navigator: ChartNavigator

    abstract fun presetChartConfiguration(configType: ChartConfigurationType)

    abstract fun displayData(
        data: IndexedDailyValues,
        referencedTimestamp: Long,
    )

    abstract fun displayEntry(dayIndex: Int)

    fun displayRange(rangeIndex: Int) = navigator.displayRange(rangeIndex)

    fun navigateRange(direction: ChartNavigator.NavigationDirection) = navigator.navigateRange(direction)
}
