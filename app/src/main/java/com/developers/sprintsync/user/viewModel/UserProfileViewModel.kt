package com.developers.sprintsync.user.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.tracking.dataStorage.repository.track.useCase.GetTracksFlowUseCase
import com.developers.sprintsync.user.model.FormattedDateRange
import com.developers.sprintsync.user.model.chart.chartData.ChartDisplayData
import com.developers.sprintsync.user.model.chart.chartData.Metric
import com.developers.sprintsync.user.model.statistics.GeneralStatistics
import com.developers.sprintsync.user.model.statistics.WeeklyStatistics
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.ChartConfigurationType
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager.ChartManager
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager.ChartManagerImpl
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation.ChartNavigator
import com.developers.sprintsync.user.ui.userProfile.util.formatter.DateRangeFormatter
import com.developers.sprintsync.user.ui.userProfile.util.formatter.GeneralStatisticsFormatter
import com.developers.sprintsync.user.ui.userProfile.util.formatter.WeeklyStatisticsFormatter
import com.github.mikephil.charting.charts.CombinedChart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel
    @Inject
    constructor(
        private val dataLoader: ChartDataLoader,
        private val timeWindowTrackFilter: TimeWindowTrackFilter,
        private val getTracksFlowUseCase: GetTracksFlowUseCase,
    ) : ViewModel() {
        private var _chartManager: ChartManager? = null
        private val chartManager get() = checkNotNull(_chartManager) { "ChartManager is not initialized" }

        private val chartDataSet get() = dataLoader.chartDataSet

        private var _dateRange: MutableStateFlow<FormattedDateRange> = MutableStateFlow(FormattedDateRange.EMPTY)
        val dateRange get() = _dateRange.asStateFlow()

        private var _selectedMetric: MutableStateFlow<Metric> = MutableStateFlow(Metric.DISTANCE)
        val selectedMetric get() = _selectedMetric.asStateFlow()

        private var _chartConfiguration: MutableStateFlow<ChartConfigurationType> =
            MutableStateFlow(ChartConfigurationType.WEEKLY)
        val chartConfiguration get() = _chartConfiguration.asStateFlow()

        private val weeklyStatisticsFormatter = WeeklyStatisticsFormatter()
        private var _weeklyStatistics: MutableStateFlow<WeeklyStatistics> = MutableStateFlow(WeeklyStatistics.EMPTY)
        val weeklyStatistics get() = _weeklyStatistics.asStateFlow()

        private val generalStatisticsFormatter = GeneralStatisticsFormatter()
        private var _generalStatistics: MutableStateFlow<GeneralStatistics> = MutableStateFlow(GeneralStatistics.EMPTY)
        val generalStatistics get() = _generalStatistics.asStateFlow()

        init {
            initTracksFlowListener()
        }

        private fun initTracksFlowListener() {
            viewModelScope.launch {
                getTracksFlowUseCase.tracks.collect { tracks ->
                    val generalStatistics = generalStatisticsFormatter.formatStatistics(tracks)
                    _generalStatistics.update { generalStatistics }
                }
            }
        }

        fun initChartManager(chart: CombinedChart) {
            _chartManager =
                ChartManagerImpl(chart).apply {
                    presetChartConfiguration(chartConfiguration.value)
                }
            initChartDataSetListener()
            initSelectedMetricListener()
            initDisplayedDataListener()
            Log.d("UserProfileViewModel", "ChartManager initialized")
        }

        fun selectMetric(metric: Metric) {
            _selectedMetric.update { metric }
        }

        fun navigateRange(direction: ChartNavigator.NavigationDirection) = chartManager.navigateRange(direction)

        fun onDestroy() {
            _chartManager = null
        }

        private fun initChartDataSetListener() {
            CoroutineScope(Dispatchers.IO).launch {
                chartDataSet.collect { chartDataSet ->
                    if (chartDataSet.data.isEmpty()) return@collect
                    val indexedValues = chartDataSet.data[selectedMetric.value]
                    if (indexedValues.isNullOrEmpty()) return@collect
                    chartManager.displayData(selectedMetric.value, indexedValues, chartDataSet.referenceTimestamp)
                }
            }
        }

        private fun initSelectedMetricListener() {
            CoroutineScope(Dispatchers.IO).launch {
                selectedMetric.collect { metric ->
                    val indexedValues = chartDataSet.value.data[metric]
                    if (indexedValues.isNullOrEmpty()) return@collect
                    chartManager.displayData(selectedMetric.value, indexedValues, chartDataSet.value.referenceTimestamp)
                }
            }
        }

        private fun initDisplayedDataListener() {
            CoroutineScope(Dispatchers.IO).launch {
                chartManager.displayData.collect { displayedData ->
                    if (displayedData == ChartDisplayData.EMPTY) return@collect
                    val referenceTimestamp = chartDataSet.value.referenceTimestamp

                    val minIndex = displayedData.data.keys.min()
                    val maxIndex = displayedData.data.keys.max()
                    val filteredTracks = timeWindowTrackFilter.filterTracks(referenceTimestamp, minIndex, maxIndex)
                    val weeklyStatistics = weeklyStatisticsFormatter.formatWeeklyStatistics(filteredTracks)

                    _weeklyStatistics.update { weeklyStatistics }

                    _dateRange.update {
                        DateRangeFormatter().formatRange(
                            referenceTimestamp,
                            displayedData.rangePosition,
                            displayedData.data.keys.min(),
                            displayedData.data.keys.max(),
                        )
                    }
                }
            }
        }

        companion object {
            private const val TAG = "My Stack: UserProfileViewModel"
        }
    }
