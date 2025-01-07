package com.developers.sprintsync.statistics.domain.chart.manager

import android.util.Log
import android.view.View
import com.developers.sprintsync.statistics.domain.chart.data.DisplayData
import com.developers.sprintsync.statistics.domain.chart.data.DailyValues
import com.developers.sprintsync.core.components.goal.data.model.Metric
import com.developers.sprintsync.statistics.domain.chart.navigator.NavigatorState
import com.developers.sprintsync.statistics.domain.chart.navigator.RangePosition
import com.developers.sprintsync.statistics.domain.chart.config.ChartConfigurationType
import com.developers.sprintsync.statistics.domain.chart.config.ChartConfigurator
import com.developers.sprintsync.statistics.domain.chart.data.calculator.ValuesCalculator
import com.developers.sprintsync.statistics.domain.chart.factory.DataConfigFactory
import com.developers.sprintsync.statistics.domain.chart.factory.WeeklyConfigFactory
import com.developers.sprintsync.statistics.domain.chart.data.transformer.CombinedDataPreparer
import com.developers.sprintsync.statistics.domain.chart.interaction.ChartGestureListener
import com.developers.sprintsync.statistics.domain.chart.navigator.ChartNavigator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CombinedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefaultChartManager(
    private val chart: CombinedChart,
) : ChartManager() {
    private var _generalData: List<DailyValues> = emptyList()

    private val _displayData: MutableStateFlow<DisplayData> = MutableStateFlow(DisplayData.EMPTY)
    override val displayData get() = _displayData.asStateFlow()

    private val configurator = ChartConfigurator(chart)
    private var chartConfigurationType: ChartConfigurationType? = null

    private var _navigator: ChartNavigator? = null
    override val navigator get() = checkNotNull(_navigator) { "Navigator is not initialized" }
    private var navigatorStateScope: CoroutineScope? = null
    private val dispatcher = Dispatchers.IO

    private val calculator = ValuesCalculator()

    private var needToShowLoading = true

    override fun presetChartConfiguration(configType: ChartConfigurationType) {
        chartConfigurationType = configType
    }

    override fun displayData(
        metric: Metric,
        data: List<DailyValues>,
        referencedTimestamp: Long,
    ) {
        if (data.isEmpty()) return
        if (_generalData == data) return
        if (needToShowLoading) {
            needToShowLoading = false
            chart.visibility = View.INVISIBLE
        }

        this._generalData = data
        val chartData = transformToCombinedData(metric, data)
        cancelNavigatorStateScope()
        chart.post {
            chart.data = chartData

            _navigator ?: initNavigator(chart) //TODO sometimes it doesn't work
            navigatorStateScope ?: initNavigatorStateListener(metric, navigator)
            chartConfigurationType?.let {
                val config =
                    WeeklyConfigFactory().createConfiguration(
                        referencedTimestamp,
                        ChartGestureListener(navigator),
                    )
                navigator.resetNavigationState()
                configurator.configureChart(config)
                resetConfig()
            }
            configurator.updateChartDisplay()
            chart.post {
                navigator.invalidate()
            }
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
                        navigator.setDisplayedRange(Int.MAX_VALUE)
                    }

                    is NavigatorState.ViewportActive -> {
                        Log.d(TAG, "NavigatorState.ViewportActive")
                        updateDisplayedData(
                            state.viewportIndices.firstDisplayedEntryIndex,
                            state.rangeLimits.chartRange,
                            state.rangePosition,
                        )
                        val displayedDataList =
                            displayData.value.data.values
                                .toList()
                        when (state) {
                            is NavigatorState.ViewportActive.InitialDisplay -> {
                                Log.d(TAG, "NavigatorState.InitialDisplay")
                                scaleUpMaximum(displayedDataList)
                                updateYAxisLabel(metric, displayedDataList)
                                configurator.updateChartDisplay()
                                withContext(Dispatchers.Main) {
                                    chart.visibility = View.VISIBLE
                                }
                            }

                            is NavigatorState.ViewportActive.Navigating -> {
                                Log.d(TAG, "NavigatorState.Navigating")
                                scaleUpMaximumAnimated(displayedDataList) {
                                    updateYAxisLabel(metric, displayedDataList)
                                    configurator.updateChartDisplay()
                                }
                            }

                            is NavigatorState.ViewportActive.DataReloaded -> {
                                Log.d(TAG, "NavigatorState.DataReloaded")
                                navigator.setDisplayedRange(Int.MAX_VALUE)
                                navigator.commitDataReload()
                                /*
                                scaleUpMaximumAnimated(displayedDataList) {
                                    updateYAxisLabel(metric, displayedDataList)
                                    configurator.refreshChart()

                                }
                                 */
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
        val dataConfigFactory = DataConfigFactory(chart.context)
        return CombinedDataPreparer().prepareCombinedData(
            data,
            dataConfigFactory.createBarConfiguration(metric, data),
            dataConfigFactory.createLineConfiguration(),
        )
    }

    private fun updateDisplayedData(
        firstVisibleEntryIndex: Int,
        range: Int,
        rangePosition: RangePosition,
    ) {
        Log.d("ChartManagerImpl", "Chart data updated")
        val toIndexExclusive = firstVisibleEntryIndex + range
        val displayedEntries =
            _generalData.subList(firstVisibleEntryIndex, toIndexExclusive.coerceAtMost(_generalData.size))
        val indexedDailyValues =
            displayedEntries
                .mapIndexed { index, dailyValues -> (firstVisibleEntryIndex + index) to dailyValues }
                .toMap()

        _displayData.value = DisplayData(
                data = indexedDailyValues,
                rangePosition = rangePosition,
            )
    }

    private fun scaleUpMaximum(displayedData: List<DailyValues>) {
        Log.d("ChartManagerImpl", "Chart maximum scaled up")
        val maxVisibleDataValue = calculator.calculateMaxOfGoalAndActualValue(displayedData)
        configurator.scaleYAxisMaximum(maxVisibleDataValue)
    }

    private fun scaleUpMaximumAnimated(
        displayedData: List<DailyValues>,
        onScalingEnd: () -> Unit,
    ) {
        val maxVisibleDataValue = calculator.calculateMaxOfGoalAndActualValue(displayedData)
        configurator.animateYAxisScaling(maxVisibleDataValue) {
            onScalingEnd()
        }
    }

    private fun updateYAxisLabel(
        metric: Metric,
        displayedData: List<DailyValues>,
    ) {
        Log.d("ChartManagerImpl", "Chart label updated")
        val label = calculator.calculateLastGoal(displayedData)
        configurator.highlightYAxisLabel(metric, label)
    }

    private fun resetConfig() {
        Log.d("ChartManagerImpl", "Chart configuration reset")
        chartConfigurationType = null
    }

    companion object {
        private const val TAG = "My stack: ChartManagerImpl"
    }
}
