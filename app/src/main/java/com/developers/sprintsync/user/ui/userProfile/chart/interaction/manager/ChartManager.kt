package com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager

import com.developers.sprintsync.user.model.chart.chartData.ChartData
import com.developers.sprintsync.user.model.chart.chartData.DailyDataPoint
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.ChartConfigurationType
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation.ChartNavigator
import com.github.mikephil.charting.charts.CombinedChart
import kotlinx.coroutines.flow.MutableStateFlow

abstract class ChartManager {

    abstract val displayedData: MutableStateFlow<List<DailyDataPoint>>

    abstract val navigator: ChartNavigator

    abstract fun presetChartConfiguration(
        configType: ChartConfigurationType,
        referencedTypeStamp: Long,
    )

    abstract fun displayData(data: ChartData)

    abstract fun displayEntry(dayIndex: Int)

    fun displayRange(rangeIndex: Int) = navigator.displayRange(rangeIndex)

    fun navigateRange(direction: ChartNavigator.NavigationDirection) = navigator.navigateRange(direction)
}
