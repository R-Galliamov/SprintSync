package com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager

import android.util.Log
import com.developers.sprintsync.user.model.chart.chartData.ChartData
import com.developers.sprintsync.user.model.chart.chartData.DailyDataPoint
import com.developers.sprintsync.user.model.chart.configuration.ChartConfiguration
import com.developers.sprintsync.user.model.chart.navigator.NavigatorState
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.ChartConfigurationType
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.configurator.ChartConfigurator
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataCalculator
import com.developers.sprintsync.user.ui.userProfile.chart.data.factory.ChartDataConfigurationFactory
import com.developers.sprintsync.user.ui.userProfile.chart.data.factory.WeekChartConfigurationFactory
import com.developers.sprintsync.user.ui.userProfile.chart.data.transformer.ChartDataPreparer
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.listener.ChartGestureListener
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation.ChartNavigator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CombinedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChartManagerImpl(
    private val chart: CombinedChart,
) : ChartManager() {
    // TODO try to map from navigator state
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
                        navigator.displayRange(Int.MAX_VALUE)
                    }

                    is NavigatorState.ViewportActive -> {
                        Log.d(TAG, "NavigatorState.ViewportActive")
                        updateDisplayedData(
                            state.viewportIndices.firstDisplayedEntryIndex,
                            state.rangeLimits.chartRange,
                        )
                        when (state) {
                            is NavigatorState.ViewportActive.InitialDisplay -> {
                                Log.d(TAG, "InitialDisplay")
                                scaleUpMaximum(displayedData.value)
                                updateYAxisLabel(displayedData.value)
                                configurator.refreshChart()
                            }

                            is NavigatorState.ViewportActive.Navigating -> {
                                Log.d(TAG, "Navigating")
                                scaleUpMaximumAnimated(displayedData.value) {
                                    updateYAxisLabel(displayedData.value)
                                    configurator.refreshChart()
                                }
                            }

                            is NavigatorState.ViewportActive.DataReloaded -> {
                                Log.d(TAG, "DataReloaded")
                                scaleUpMaximumAnimated(displayedData.value) {
                                    updateYAxisLabel(displayedData.value)
                                    configurator.refreshChart()
                                    navigator.commitDataReload()
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
            dailyPoints.subList(firstVisibleEntryIndex, toIndexExclusive)
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
