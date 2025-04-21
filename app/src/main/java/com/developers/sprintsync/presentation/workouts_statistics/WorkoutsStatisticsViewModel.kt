package com.developers.sprintsync.presentation.workouts_statistics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.domain.goal.model.DailyGoal
import com.developers.sprintsync.domain.goal.model.Metric
import com.developers.sprintsync.domain.goal.use_case.GetDailyGoalsFlowUseCase
import com.developers.sprintsync.domain.goal.use_case.GetDailyGoalsUpdateTimestampUseCase
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.use_case.storage.GetTracksFlowUseCase
import com.developers.sprintsync.presentation.workouts_statistics.chart.config.ChartConfigurationType
import com.developers.sprintsync.presentation.workouts_statistics.chart.data.ChartDataSet
import com.developers.sprintsync.presentation.workouts_statistics.chart.data.DisplayData
import com.developers.sprintsync.presentation.workouts_statistics.chart.data.WeekDay
import com.developers.sprintsync.presentation.workouts_statistics.chart.data.processing.WeeklyChartDataPreparer
import com.developers.sprintsync.presentation.workouts_statistics.data.ChartDataUpdateEvent
import com.developers.sprintsync.presentation.workouts_statistics.util.filter.TimeWindowTrackFilter
import com.developers.sprintsync.presentation.workouts_statistics.data.DateRange
import com.developers.sprintsync.presentation.workouts_statistics.data.GeneralStatistics
import com.developers.sprintsync.presentation.workouts_statistics.data.WeeklyStatistics
import com.developers.sprintsync.presentation.workouts_statistics.util.formatter.UpdateDateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO create class to handle chart data
// TODO use state machine
@HiltViewModel
class WorkoutsStatisticsViewModel
    @Inject
    constructor(
        private val timeWindowTrackFilter: TimeWindowTrackFilter,
        private val getTracksFlowUseCase: GetTracksFlowUseCase,
        private val getDailyGoalsFLowUseCase: GetDailyGoalsFlowUseCase,
        getDailyGoalsUpdateTimestampUseCase: GetDailyGoalsUpdateTimestampUseCase,
        private val chartDataPreparer: WeeklyChartDataPreparer,
        private val dateFormatter: UpdateDateFormatter,
    ) : ViewModel() {
        private val _chartDataUpdateEvent = MutableStateFlow<ChartDataUpdateEvent?>(null)
        val chartDataUpdateEvent get() = _chartDataUpdateEvent.asStateFlow().filterNotNull()

        val dailyGoalsUpdateDate =
            getDailyGoalsUpdateTimestampUseCase().map { dateFormatter.formatTimestamp(it) }

        private val goalsState = MutableStateFlow<List<DailyGoal>>(emptyList())

        private val tracksState = MutableStateFlow<List<Track>>(emptyList())

        private var _dateRange: MutableStateFlow<DateRange> = MutableStateFlow(DateRange.EMPTY)
        val dateRange get() = _dateRange.asStateFlow()

        private var _selectedMetric: MutableStateFlow<Metric> = MutableStateFlow(Metric.DISTANCE)
        val selectedMetric get() = _selectedMetric.asStateFlow()

        private var _chartConfiguration: MutableStateFlow<ChartConfigurationType> =
            MutableStateFlow(ChartConfigurationType.WEEKLY)
        val chartConfiguration get() = _chartConfiguration.asStateFlow()

        private var _weeklyStatistics: MutableStateFlow<WeeklyStatistics> = MutableStateFlow(WeeklyStatistics.EMPTY)
        val weeklyStatistics get() = _weeklyStatistics.asStateFlow()

        private var _generalStatistics: MutableStateFlow<GeneralStatistics> = MutableStateFlow(GeneralStatistics.EMPTY)
        val generalStatistics get() = _generalStatistics.asStateFlow()

        private val chartDataSet =
            MutableStateFlow(ChartDataSet.EMPTY)

        init {
            initSelectedMetricListener()
            initChartDataSetListener()
            initTracksFlowListener()
            initGoalsFlowListener()
        }

        fun selectMetric(metric: Metric) {
            _selectedMetric.update { metric }
        }

        fun onDisplayedDataChanged(displayedData: DisplayData) {
            if (displayedData == DisplayData.EMPTY) return
            val referenceTimestamp = chartDataSet.value.referenceTimestamp

            val minIndex = displayedData.data.keys.min()
            val maxIndex = displayedData.data.keys.max()
            val filteredTracks =
                timeWindowTrackFilter.filterTracks(tracksState.value, referenceTimestamp, minIndex, maxIndex)
            val weeklyStatistics = WeeklyStatistics.create(filteredTracks)

            _weeklyStatistics.update {
                weeklyStatistics
            }

            _dateRange.update {
                DateRange.create(
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
                    if (tracks.isEmpty()) return@collect // TODO remove collect as empty list should return empty statistics
                    tracksState.update { tracks }

                    _generalStatistics.update { GeneralStatistics.create(tracks) }

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
