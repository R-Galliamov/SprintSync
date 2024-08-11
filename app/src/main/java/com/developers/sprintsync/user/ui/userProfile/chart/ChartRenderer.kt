package com.developers.sprintsync.user.ui.userProfile.chart

import android.util.Log
import com.developers.sprintsync.global.util.extension.combineAndCollectLatest
import com.developers.sprintsync.user.model.chart.ChartData
import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.ChartConfigurator
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataCalculator
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataConfigurationFactory
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataPreparer
import com.developers.sprintsync.user.ui.userProfile.chart.data.WeekChartConfigurationFactory
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.listener.ChartGestureListener
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation.ChartNavigator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CombinedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    // TODO Should navigator be reset after switch the configuration?
    private val navigator: ChartNavigator by lazy { ChartNavigator(chart) }

    private var dailyPoints: List<DailyDataPoint> = listOf()

    override val displayedData: MutableStateFlow<List<DailyDataPoint>> = MutableStateFlow(emptyList())

    private var navigatorStateScope: CoroutineScope? = null
    private val dispatcher = Dispatchers.IO

    private val configurator = ChartConfigurator(chart)

    private val calculator = ChartDataCalculator()

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
                configurator.presetConfig(config)
            }
        }
    }

    override fun displayData(data: ChartData) {
        this.dailyPoints = data.dailyPoints

        val chartData = transformToCombinedData(data)
        chart.data = chartData

        configurator.applyConfiguration()
        configurator.refreshChart()

        navigator.invalidate()

        if (navigatorStateScope == null) {
            initNavigatorStateListener()
            displayRange(Int.MIN_VALUE)
        }
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
            navigator.rangeLimits.combineAndCollectLatest(
                navigator.indices,
                CoroutineScope(Dispatchers.IO),
                { limits, indices -> Pair(limits, indices) },
            ) { result ->
                navigator.executeIfDataLoaded(result.first, result.second) { loadedLimits, loadedIndices ->
                    updateDisplayedData(
                        loadedIndices.firstDisplayedEntryIndex,
                        loadedLimits.chartRange,
                    )
                    navigatorStateScope?.launch {
                        withContext(Dispatchers.Main) {
                            scaleUpMaximum(displayedData.value) {
                                updateYAxisLabel(displayedData.value)
                                configurator.refreshChart()
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

    private fun scaleUpMaximum(
        displayedData: List<DailyDataPoint>,
        onScalingEnd: () -> Unit,
    ) {
        Log.d("My stack: ChartManagerImpl", "$displayedData")
        val maxVisibleDataValue = calculator.calculateMaxOfGoalAndActualValue(displayedData)
        Log.d("My stack: ChartManagerImpl", "scaleUpMaximum: $maxVisibleDataValue")
        configurator.scaleUpMaximum(maxVisibleDataValue) {
            onScalingEnd()
        }
    }

    private fun updateYAxisLabel(displayedData: List<DailyDataPoint>) {
        val label = calculator.calculateLastGoal(displayedData)
        configurator.selectYLabel(label)
    }
}

class DataHandlerClass {
    fun getListOfData(
        data: List<DailyDataPoint>,
        fromIndexInclusive: Int,
        toIndexExclusive: Int,
    ): List<DailyDataPoint> {
        return data.subList(fromIndexInclusive, toIndexExclusive)
    }
}

enum class ChartConfigurationType {
    WEEKLY,
}
