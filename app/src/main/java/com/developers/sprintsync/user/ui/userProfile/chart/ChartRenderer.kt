package com.developers.sprintsync.user.ui.userProfile.chart

import android.util.Log
import com.developers.sprintsync.user.model.chart.ChartData
import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.model.chart.configuration.ChartConfiguration
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.ChartConfigurator
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataCalculator
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataConfigurationFactory
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataPreparer
import com.developers.sprintsync.user.ui.userProfile.chart.data.WeekChartConfigurationFactory
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.listener.ChartGestureListener
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation.ChartNavigator
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation.NavigatorState
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CombinedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class ChartManager {
    abstract val displayedData: MutableStateFlow<List<DailyDataPoint>>

    abstract fun presetChartConfiguration(
        configType: ChartConfigurationType,
        referencedTypeStamp: Long,
    )

    abstract fun displayData(data: ChartData)

    abstract fun displayEntry(dayIndex: Int)

    abstract fun displayRange(rangeIndex: Int)
}

class ChartManagerImpl(
    private val chart: CombinedChart,
) : ChartManager() {
    private var dailyPoints: List<DailyDataPoint> = listOf()

    override val displayedData: MutableStateFlow<List<DailyDataPoint>> = MutableStateFlow(emptyList())

    private var navigatorStateScope: CoroutineScope? = null
    private val dispatcher = Dispatchers.IO

    private val configurator = ChartConfigurator(chart)

    private val calculator = ChartDataCalculator()

    private var chartConfigurationToBeApplied: ChartConfiguration? = null

    init {
        initNavigatorStateListener()
    }

    private val navigator: ChartNavigator by lazy { ChartNavigator(chart) }

    override fun presetChartConfiguration(
        configType: ChartConfigurationType,
        referencedTypeStamp: Long,
    ) {
        when (configType) {
            ChartConfigurationType.WEEKLY -> {
                val config =
                    WeekChartConfigurationFactory().createConfiguration(
                        referencedTypeStamp,
                        ChartGestureListener(navigator),
                    )
                chartConfigurationToBeApplied = config
            }
        }
    }

    override fun displayData(data: ChartData) {
        this.dailyPoints = data.dailyPoints

        val chartData = transformToCombinedData(data)
        chart.data = chartData

        chartConfigurationToBeApplied?.let {
            navigator.resetToInitial()
            configurator.applyConfiguration(it)
            resetConfig()
        }

        configurator.refreshChart()
        navigator.invalidate()
    }

    override fun displayEntry(dayIndex: Int) {
        TODO("Not yet implemented")
    }

    override fun displayRange(rangeIndex: Int) {
        navigator.displayRange(rangeIndex)
    }

    private fun initNavigatorStateScope() {
        navigatorStateScope = CoroutineScope(dispatcher)
    }

    private fun initNavigatorStateListener() {
        initNavigatorStateScope()

        navigatorStateScope?.launch {
            navigator.state.collect { state ->
                when (state) {
                    is NavigatorState.Initialised -> {
                        Log.d(TAG, "NavigatorState.Initialised")
                    }

                    is NavigatorState.DataLoaded -> { // Keep showing loading bar
                        Log.d(TAG, "NavigatorState.DataLoaded")
                    }

                    is NavigatorState.ViewportActive -> {
                        Log.d(TAG, "NavigatorState.ViewportActive")
                        updateDisplayedData(
                            state.viewportIndices.firstDisplayedEntryIndex,
                            state.rangeLimits.chartRange,
                        )
                        when (state) {
                            is NavigatorState.ViewportActive.InitialDisplay -> {
                                navigator.displayRange(Int.MAX_VALUE)
                                scaleUpMaximum(displayedData.value)
                                updateYAxisLabel(displayedData.value)
                                configurator.refreshChart()
                            }

                            is NavigatorState.ViewportActive.Navigating -> {
                                scaleUpMaximumAnimated(displayedData.value) {
                                    updateYAxisLabel(displayedData.value)
                                    configurator.refreshChart()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun transformToCombinedData(data: ChartData): CombinedData {
        val dataConfigFactory = ChartDataConfigurationFactory(chart.context)
        return ChartDataPreparer().prepareCombinedData(
            data.dailyPoints,
            dataConfigFactory.createBarConfiguration(data),
            dataConfigFactory.createLineConfiguration(data),
        )
    }

    private fun updateDisplayedData(
        firstVisibleEntryIndex: Int,
        range: Int,
    ) {
        val toIndexExclusive = firstVisibleEntryIndex + range
        val displayedEntries =
            DataHandlerClass().getListOfData(dailyPoints, firstVisibleEntryIndex, toIndexExclusive)
        displayedData.value = displayedEntries
    }

    private fun scaleUpMaximum(displayedData: List<DailyDataPoint>) {
        val maxVisibleDataValue = calculator.calculateMaxOfGoalAndActualValue(displayedData)
        configurator.scaleUpMaximum(maxVisibleDataValue)
    }

    private fun scaleUpMaximumAnimated(
        displayedData: List<DailyDataPoint>,
        onScalingEnd: () -> Unit,
    ) {
        val maxVisibleDataValue = calculator.calculateMaxOfGoalAndActualValue(displayedData)
        configurator.scaleUpMaximumAnimated(maxVisibleDataValue) {
            onScalingEnd()
        }
    }

    private fun updateYAxisLabel(displayedData: List<DailyDataPoint>) {
        val label = calculator.calculateLastGoal(displayedData)
        configurator.selectYLabel(label)
    }

    private fun resetConfig() {
        chartConfigurationToBeApplied = null
    }

    companion object {
        private const val TAG = "My stack: ChartManagerImpl"
    }
}

class DataHandlerClass {
    fun getListOfData(
        data: List<DailyDataPoint>,
        fromIndexInclusive: Int,
        toIndexExclusive: Int,
    ): List<DailyDataPoint> = data.subList(fromIndexInclusive, toIndexExclusive)
}

enum class ChartConfigurationType {
    WEEKLY,
}
