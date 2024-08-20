package com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager

import android.util.Log
import com.developers.sprintsync.user.model.chart.chartData.IndexedDailyValues
import com.developers.sprintsync.user.model.chart.navigator.NavigatorState
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.ChartConfigurationType
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.configurator.ChartConfigurator
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartValuesCalculator
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
    private var dailyPoints: IndexedDailyValues = mapOf()

    override val displayedData: MutableStateFlow<IndexedDailyValues> = MutableStateFlow(mapOf())

    private var navigatorStateScope: CoroutineScope? = null
    private val dispatcher = Dispatchers.IO

    private val configurator = ChartConfigurator(chart)

    private val calculator = ChartValuesCalculator()

    private var chartConfigurationType: ChartConfigurationType? = null

    private var _navigator: ChartNavigator? = null

    override val navigator get() = checkNotNull(_navigator) { "Navigator is not initialized" }

    override fun presetChartConfiguration(configType: ChartConfigurationType) {
        chartConfigurationType = configType
    }

    override fun displayData(
        data: IndexedDailyValues,
        referencedTimestamp: Long,
    ) {
        Log.d("ChartManagerImpl", "Try to display data")
        if (data.isEmpty()) return
        if (dailyPoints == data) return
        Log.d("ChartManagerImpl", "Chart data displayed")

        this.dailyPoints = data

        val chartData = transformToCombinedData(data)
        chart.data = chartData

        _navigator ?: let {
            initNavigator(chart)
            initNavigatorStateListener(navigator)
        }

        chartConfigurationType?.let {
            val config =
                WeekChartConfigurationFactory().createConfiguration(
                    referencedTimestamp,
                    ChartGestureListener(navigator),
                )

            navigator.resetToInitial()
            configurator.applyConfiguration(config)
            resetConfig()
        }

        configurator.refreshChart()
        navigator.invalidate()
    }

    override fun displayEntry(dayIndex: Int) {
        TODO("Not yet implemented")
    }

    private fun initNavigator(chart: CombinedChart) {
        Log.d("ChartManagerImpl", "Navigator initialized")
        _navigator = ChartNavigator(chart)
    }

    private fun initNavigatorStateScope() {
        Log.d("ChartManagerImpl", "NavigatorStateScope initialized")
        navigatorStateScope = CoroutineScope(dispatcher)
    }

    private fun initNavigatorStateListener(navigator: ChartNavigator) {
        Log.d("ChartManagerImpl", "NavigatorStateListener initialized")
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

    private fun transformToCombinedData(data: IndexedDailyValues): CombinedData {
        Log.d("ChartManagerImpl", "Chart data transformed")
        val dataConfigFactory = ChartDataConfigurationFactory(chart.context)
        return ChartDataPreparer().prepareCombinedData(
            data,
            dataConfigFactory.createBarConfiguration(data.values.toList()),
            dataConfigFactory.createLineConfiguration(),
        )
    }

    private fun updateDisplayedData(
        firstVisibleEntryIndex: Int,
        range: Int,
    ) {
        Log.d("ChartManagerImpl", "Chart data updated")
        val toIndexExclusive = firstVisibleEntryIndex + range
        val displayedEntries =
            dailyPoints.filterKeys { it in firstVisibleEntryIndex until toIndexExclusive }
        displayedData.value = displayedEntries
    }

    private fun scaleUpMaximum(displayedData: IndexedDailyValues) {
        Log.d("ChartManagerImpl", "Chart maximum scaled up")
        val maxVisibleDataValue = calculator.calculateMaxOfGoalAndActualValue(displayedData.values.toList())
        configurator.scaleUpMaximum(maxVisibleDataValue)
    }

    private fun scaleUpMaximumAnimated(
        displayedData: IndexedDailyValues,
        onScalingEnd: () -> Unit,
    ) {
        Log.d("ChartManagerImpl", "Chart maximum scaled up animated")
        val maxVisibleDataValue = calculator.calculateMaxOfGoalAndActualValue(displayedData.values.toList())
        configurator.scaleUpMaximumAnimated(maxVisibleDataValue) {
            onScalingEnd()
        }
    }

    private fun updateYAxisLabel(displayedData: IndexedDailyValues) {
        Log.d("ChartManagerImpl", "Chart label updated")
        val label = calculator.calculateLastGoal(displayedData.values.toList())
        configurator.selectYLabel(label)
    }

    private fun resetConfig() {
        Log.d("ChartManagerImpl", "Chart configuration reset")
        chartConfigurationType = null
    }

    companion object {
        private const val TAG = "My stack: ChartManagerImpl"
    }
}
