package com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager

import android.util.Log
import com.developers.sprintsync.user.model.chart.configuration.ChartConfiguration
import com.developers.sprintsync.user.model.chart.navigator.NavigatorState
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.ChartConfigurationType
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.configurator.ChartConfigurator
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataCalculator
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataPoints
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
    private var dailyPoints: ChartDataPoints = mapOf()

    override val displayedData: MutableStateFlow<ChartDataPoints> = MutableStateFlow(mapOf())

    private var navigatorStateScope: CoroutineScope? = null
    private val dispatcher = Dispatchers.IO

    private val configurator = ChartConfigurator(chart)

    private val calculator = ChartDataCalculator()

    private var chartConfigurationToBeApplied: ChartConfiguration? = null

    init {
        initNavigatorStateListener()
    }

    // TODO was crash here
    // FATAL EXCEPTION: DefaultDispatcher-worker-1 (Ask Gemini)
    //   Process: com.developers.sprintsync, PID: 5674
    //   java.lang.NullPointerException: Attempt to invoke interface method 'java.lang.Object kotlin.Lazy.getValue()' on a null object reference
    //   	at com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager.ChartManagerImpl.getNavigator(ChartManagerImpl.kt:42)
    //   	at com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager.ChartManagerImpl$initNavigatorStateListener$1.invokeSuspend(ChartManagerImpl.kt:88)
    //   	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
    //   	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
    //   	at kotlinx.coroutines.internal.LimitedDispatcher$Worker.run(LimitedDispatcher.kt:115)
    //   	at kotlinx.coroutines.scheduling.TaskImpl.run(Tasks.kt:103)
    //   	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:584)
    //   	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:793)
    //   	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:697)
    //   	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:684)
    //   	Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [StandaloneCoroutine{Cancelling}@859e2c8, Dispatchers.IO]

    override val navigator: ChartNavigator by lazy { ChartNavigator(chart) }

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

    override fun displayData(data: ChartDataPoints) {
        this.dailyPoints = data

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

    private fun transformToCombinedData(data: ChartDataPoints): CombinedData {
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
        val toIndexExclusive = firstVisibleEntryIndex + range
        val displayedEntries =
            dailyPoints.filterKeys { it in firstVisibleEntryIndex until toIndexExclusive }
        displayedData.value = displayedEntries
    }

    private fun scaleUpMaximum(displayedData: ChartDataPoints) {
        val maxVisibleDataValue = calculator.calculateMaxOfGoalAndActualValue(displayedData.values.toList())
        configurator.scaleUpMaximum(maxVisibleDataValue)
    }

    private fun scaleUpMaximumAnimated(
        displayedData: ChartDataPoints,
        onScalingEnd: () -> Unit,
    ) {
        val maxVisibleDataValue = calculator.calculateMaxOfGoalAndActualValue(displayedData.values.toList())
        configurator.scaleUpMaximumAnimated(maxVisibleDataValue) {
            onScalingEnd()
        }
    }

    private fun updateYAxisLabel(displayedData: ChartDataPoints) {
        val label = calculator.calculateLastGoal(displayedData.values.toList())
        configurator.selectYLabel(label)
    }

    private fun resetConfig() {
        chartConfigurationToBeApplied = null
    }

    companion object {
        private const val TAG = "My stack: ChartManagerImpl"
    }
}
