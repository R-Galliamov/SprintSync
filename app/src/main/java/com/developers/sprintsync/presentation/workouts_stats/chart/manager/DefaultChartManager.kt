package com.developers.sprintsync.presentation.workouts_stats.chart.manager

import android.view.View
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.log.TimberAppLogger
import com.developers.sprintsync.domain.workouts_plan.model.Metric
import com.developers.sprintsync.presentation.workouts_stats.chart.config.ChartConfigurationType
import com.developers.sprintsync.presentation.workouts_stats.chart.config.ChartConfigurator
import com.developers.sprintsync.presentation.workouts_stats.chart.data.DailyValues
import com.developers.sprintsync.presentation.workouts_stats.chart.data.DisplayData
import com.developers.sprintsync.presentation.workouts_stats.chart.data.getActualValues
import com.developers.sprintsync.presentation.workouts_stats.chart.data.transformer.CombinedDataPreparer
import com.developers.sprintsync.presentation.workouts_stats.chart.factory.DataConfigFactory
import com.developers.sprintsync.presentation.workouts_stats.chart.factory.WeeklyConfigFactory
import com.developers.sprintsync.presentation.workouts_stats.chart.interaction.ChartGestureListener
import com.developers.sprintsync.presentation.workouts_stats.chart.navigator.ChartNavigator
import com.developers.sprintsync.presentation.workouts_stats.chart.navigator.NavigatorState
import com.developers.sprintsync.presentation.workouts_stats.chart.navigator.RangePosition
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
    private val log: AppLogger = TimberAppLogger(),
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

    private var needToShowLoading = true

    override fun presetChart(configType: ChartConfigurationType) {
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

            _navigator ?: initNavigator(chart)
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

    private fun initNavigator(chart: CombinedChart) {
        log.d("Navigator initialized")
        _navigator = ChartNavigator(chart)
    }

    private fun initNavigatorStateScope() {
        log.d("NavigatorStateScope initialized")
        navigatorStateScope = CoroutineScope(dispatcher)
    }

    private fun cancelNavigatorStateScope() {
        log.d("NavigatorStateScope canceled")
        navigatorStateScope?.cancel()
        navigatorStateScope = null
    }

    private fun initNavigatorStateListener(
        metric: Metric,
        navigator: ChartNavigator,
    ) {
        initNavigatorStateScope()

        navigatorStateScope?.launch {
            navigator.state.collect { state ->
                when (state) {
                    is NavigatorState.Initialised -> {
                        /* NO-IP */
                    }

                    is NavigatorState.DataLoaded -> { // Keep showing loading bar
                        navigator.setDisplayedRange(Int.MAX_VALUE)
                    }

                    is NavigatorState.ViewportActive -> {
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
                                scaleUpMaximum(displayedDataList)
                                updateYAxisLabel(metric, displayedDataList)
                                configurator.updateChartDisplay()
                                withContext(Dispatchers.Main) {
                                    chart.visibility = View.VISIBLE
                                }
                            }

                            is NavigatorState.ViewportActive.Navigating -> {
                                scaleUpMaximumAnimated(displayedDataList) {
                                    updateYAxisLabel(metric, displayedDataList)
                                    configurator.updateChartDisplay()
                                }
                            }

                            is NavigatorState.ViewportActive.DataReloaded -> {
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
        val maxValue = getMaxValue(displayedData)
        configurator.scaleYAxisMaximum(maxValue)
    }

    private fun scaleUpMaximumAnimated(
        displayedData: List<DailyValues>,
        onScalingEnd: () -> Unit,
    ) {
        val maxVisibleDataValue =
            displayedData.filterIsInstance<DailyValues.Present>().maxOfOrNull { it.actualValue } ?: 0f
        configurator.animateYAxisScaling(maxVisibleDataValue) {
            onScalingEnd()
        }
    }

    private fun updateYAxisLabel(
        metric: Metric,
        displayedData: List<DailyValues>,
    ) {
        val label = getAverage(displayedData)
        configurator.highlightYAxisLabel(metric, label)
    }

    private fun resetConfig() {
        chartConfigurationType = null
    }

    private fun getAverage(displayedData: List<DailyValues>): Float {
        val presentValues = displayedData.getActualValues()
        return presentValues.average().toFloat()
    }

    private fun getMaxValue(displayedData: List<DailyValues>): Float {
        return displayedData.getActualValues().maxOrNull() ?: 0f
    }

}
