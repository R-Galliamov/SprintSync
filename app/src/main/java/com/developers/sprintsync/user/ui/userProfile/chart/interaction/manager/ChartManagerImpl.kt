package com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager

import android.util.Log
import com.developers.sprintsync.user.model.chart.chartData.DailyValues
import com.developers.sprintsync.user.model.chart.chartData.Metric
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChartManagerImpl(
    private val chart: CombinedChart,
) : ChartManager() {
    private var generalDailyValues: List<DailyValues> = emptyList()

    private val _displayedData: MutableStateFlow<Map<Int, DailyValues>> = MutableStateFlow(emptyMap())
    override val displayedData get() = _displayedData.asStateFlow()

    private val configurator = ChartConfigurator(chart)
    private var chartConfigurationType: ChartConfigurationType? = null

    private var _navigator: ChartNavigator? = null
    override val navigator get() = checkNotNull(_navigator) { "Navigator is not initialized" }
    private var navigatorStateScope: CoroutineScope? = null
    private val dispatcher = Dispatchers.IO

    private val calculator = ChartValuesCalculator()

    override fun presetChartConfiguration(configType: ChartConfigurationType) {
        chartConfigurationType = configType
    }

    override fun displayData(
        metric: Metric,
        data: List<DailyValues>,
        referencedTimestamp: Long,
    ) {
        Log.d("ChartManagerImpl", "Try to display data")
        if (data.isEmpty()) return
        if (generalDailyValues == data) return
        Log.d("ChartManagerImpl", "Chart data displayed")
        this.generalDailyValues = data
        val chartData = transformToCombinedData(metric, data)
        cancelNavigatorStateScope()
        chart.post {
            chart.data = chartData
            _navigator ?: initNavigator(chart)
            navigatorStateScope ?: initNavigatorStateListener(metric, navigator)
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

    private fun cancelNavigatorStateScope() {
        Log.d("ChartManagerImpl", "NavigatorStateScope canceled")
        navigatorStateScope?.cancel()
        navigatorStateScope = null
    }

    private fun initNavigatorStateListener(
        metric: Metric,
        navigator: ChartNavigator,
    ) {
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
                        val displayedDataList = displayedData.value.values.toList()
                        when (state) {
                            is NavigatorState.ViewportActive.InitialDisplay -> {
                                Log.d(TAG, "InitialDisplay")
                                scaleUpMaximum(displayedDataList)
                                updateYAxisLabel(metric, displayedDataList)
                                configurator.refreshChart()
                            }

                            is NavigatorState.ViewportActive.Navigating -> {
                                Log.d(TAG, "Navigating")
                                scaleUpMaximumAnimated(displayedDataList) {
                                    updateYAxisLabel(metric, displayedDataList)
                                    configurator.refreshChart()
                                }
                            }

                            is NavigatorState.ViewportActive.DataReloaded -> {
                                Log.d(TAG, "DataReloaded")
                                scaleUpMaximumAnimated(displayedDataList) {
                                    updateYAxisLabel(metric, displayedDataList)
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

    private fun transformToCombinedData(
        metric: Metric,
        data: List<DailyValues>,
    ): CombinedData {
        Log.d("ChartManagerImpl", "Chart data transformed")
        val dataConfigFactory = ChartDataConfigurationFactory(chart.context)
        return ChartDataPreparer().prepareCombinedData(
            data,
            dataConfigFactory.createBarConfiguration(metric, data),
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
            generalDailyValues.subList(firstVisibleEntryIndex, toIndexExclusive.coerceAtMost(generalDailyValues.size))
        _displayedData.value =
            displayedEntries
                .mapIndexed { index, dailyValues -> (firstVisibleEntryIndex + index) to dailyValues }
                .toMap()
    }

    private fun scaleUpMaximum(displayedData: List<DailyValues>) {
        Log.d("ChartManagerImpl", "Chart maximum scaled up")
        val maxVisibleDataValue = calculator.calculateMaxOfGoalAndActualValue(displayedData)
        configurator.scaleUpMaximum(maxVisibleDataValue)
    }

    private fun scaleUpMaximumAnimated(
        displayedData: List<DailyValues>,
        onScalingEnd: () -> Unit,
    ) {
        Log.d("ChartManagerImpl", "Chart maximum scaled up animated")
        val maxVisibleDataValue = calculator.calculateMaxOfGoalAndActualValue(displayedData)
        configurator.scaleUpMaximumAnimated(maxVisibleDataValue) {
            onScalingEnd()
        }
    }

    private fun updateYAxisLabel(
        metric: Metric,
        displayedData: List<DailyValues>,
    ) {
        Log.d("ChartManagerImpl", "Chart label updated")
        val label = calculator.calculateLastGoal(displayedData)
        configurator.selectYLabel(metric, label)
    }

    private fun resetConfig() {
        Log.d("ChartManagerImpl", "Chart configuration reset")
        chartConfigurationType = null
    }

    companion object {
        private const val TAG = "My stack: ChartManagerImpl"
    }
}
