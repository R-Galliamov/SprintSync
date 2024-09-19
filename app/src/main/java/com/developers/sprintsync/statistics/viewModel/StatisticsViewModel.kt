package com.developers.sprintsync.statistics.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.statistics.dataStorage.repository.dailyGoal.useCase.GetDailyGoalsFlowUseCase
import com.developers.sprintsync.statistics.dataStorage.repository.dailyGoal.useCase.GetDailyGoalsUpdateTimestampUseCase
import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.statistics.model.chart.chartData.WeekDay
import com.developers.sprintsync.statistics.model.goal.DailyGoal
import com.developers.sprintsync.statistics.model.statistics.GeneralStatistics
import com.developers.sprintsync.statistics.model.statistics.WeeklyStatistics
import com.developers.sprintsync.statistics.model.ui.ChartDataUpdateEvent
import com.developers.sprintsync.statistics.model.ui.FormattedDateRange
import com.developers.sprintsync.statistics.ui.statistics.chart.configuration.ChartConfigurationType
import com.developers.sprintsync.statistics.ui.statistics.chart.data.preparation.ChartWeeklyDataPreparer
import com.developers.sprintsync.statistics.ui.statistics.util.formatter.DateRangeFormatter
import com.developers.sprintsync.statistics.ui.statistics.util.formatter.GeneralStatisticsFormatter
import com.developers.sprintsync.statistics.ui.statistics.util.formatter.UpdateDateFormatter
import com.developers.sprintsync.statistics.ui.statistics.util.formatter.WeeklyStatisticsFormatter
import com.developers.sprintsync.statistics.util.filter.TimeWindowTrackFilter
import com.developers.sprintsync.tracking.dataStorage.repository.track.useCase.GetTracksFlowUseCase
import com.developers.sprintsync.tracking.session.model.track.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel
    @Inject
    constructor(
        private val timeWindowTrackFilter: TimeWindowTrackFilter,
        private val getTracksFlowUseCase: GetTracksFlowUseCase,
        private val getDailyGoalsFLowUseCase: GetDailyGoalsFlowUseCase,
        getDailyGoalsUpdateTimestampUseCase: GetDailyGoalsUpdateTimestampUseCase,
        private val chartDataPreparer: ChartWeeklyDataPreparer,
        private val dateFormatter: UpdateDateFormatter,
    ) : ViewModel() {
        private val _chartDataUpdateEvent = MutableStateFlow<ChartDataUpdateEvent?>(null)
        val chartDataUpdateEvent get() = _chartDataUpdateEvent

        val dailyGoalsUpdateDate =
            getDailyGoalsUpdateTimestampUseCase().map { dateFormatter.formatTimestamp(it) }

        private val goalsState = MutableStateFlow<List<DailyGoal>>(emptyList())

        private val tracksState = MutableStateFlow<List<Track>>(emptyList())

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

        private val chartDataSet =
            MutableStateFlow(com.developers.sprintsync.statistics.model.chart.chartData.ChartDataSet.EMPTY)

        init {
            initSelectedMetricListener()
            initChartDataSetListener()
            initTracksFlowListener()
            initGoalsFlowListener()
        }

        fun selectMetric(metric: Metric) {
            _selectedMetric.update { metric }
        }

        fun onDisplayedDataChanged(displayedData: com.developers.sprintsync.statistics.model.chart.chartData.ChartDisplayData) {
            if (displayedData == com.developers.sprintsync.statistics.model.chart.chartData.ChartDisplayData.EMPTY) return
            val referenceTimestamp = chartDataSet.value.referenceTimestamp

            val minIndex = displayedData.data.keys.min()
            val maxIndex = displayedData.data.keys.max()
            val filteredTracks =
                timeWindowTrackFilter.filterTracks(tracksState.value, referenceTimestamp, minIndex, maxIndex)
            val weeklyStatistics = weeklyStatisticsFormatter.formatWeeklyStatistics(filteredTracks)

            _weeklyStatistics.update {
                weeklyStatistics
            }

            _dateRange.update {
                DateRangeFormatter().formatRange(
                    referenceTimestamp,
                    displayedData.rangePosition,
                    displayedData.data.keys.min(),
                    displayedData.data.keys.max(),
                )
            }
        }

        private fun initTracksFlowListener() {
            viewModelScope.launch {
                getTracksFlowUseCase.tracks.collect { tracks ->
                    if (tracks.isEmpty()) return@collect
                    tracksState.update { tracks }

                    val generalStatistics = generalStatisticsFormatter.formatStatistics(tracks)
                    _generalStatistics.update { generalStatistics }

                    chartDataSet.update {
                        chartDataPreparer.prepareChartSet(tracks, goalsState.value, WeekDay.MONDAY)
                    }
                }
            }
        }

        private fun initGoalsFlowListener() {
            viewModelScope.launch {
                getDailyGoalsFLowUseCase.dailyGoals.collect { goals ->
                    goalsState.update {
                        Log.d(TAG, "initGoalsFlowListener: $goals")
                        goals
                    }
                    chartDataSet.update {
                        Log.d(TAG, "initGoalsFlowListener, start updating chart $it")
                        chartDataPreparer.prepareChartSet(tracksState.value, goals, WeekDay.MONDAY)
                    }
                }
            }
        }

        private fun initChartDataSetListener() {
            viewModelScope.launch {
                chartDataSet.collect { chartDataSet ->
                    Log.d(TAG, "ChartDataSet updated: $chartDataSet")
                    if (chartDataSet.data.isEmpty()) return@collect
                    val indexedValues = chartDataSet.data[selectedMetric.value]
                    if (indexedValues.isNullOrEmpty()) return@collect

                    _chartDataUpdateEvent.update {
                        ChartDataUpdateEvent(
                            selectedMetric.value,
                            indexedValues,
                            chartDataSet.referenceTimestamp,
                        )
                    }
                }
            }
        }

        private fun initSelectedMetricListener() {
            viewModelScope.launch {
                selectedMetric.collect { metric ->
                    val indexedValues = chartDataSet.value.data[metric]
                    if (indexedValues.isNullOrEmpty()) return@collect
                    _chartDataUpdateEvent.update {
                        ChartDataUpdateEvent(
                            selectedMetric.value,
                            indexedValues,
                            chartDataSet.value.referenceTimestamp,
                        )
                    }
                }
            }
        }

        companion object {
            private const val TAG = "My Stack: UserProfileViewModel"
        }
    }