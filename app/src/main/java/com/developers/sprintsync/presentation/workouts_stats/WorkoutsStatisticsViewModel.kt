package com.developers.sprintsync.presentation.workouts_stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.data.statistics.WorkoutsStatsCreator
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.use_case.storage.GetTracksFlowUseCase
import com.developers.sprintsync.presentation.components.Metric
import com.developers.sprintsync.presentation.workouts_stats.chart.config.ChartConfigurationType
import com.developers.sprintsync.presentation.workouts_stats.chart.data.ChartDataSet
import com.developers.sprintsync.presentation.workouts_stats.chart.data.DisplayData
import com.developers.sprintsync.presentation.workouts_stats.chart.data.WeekDay
import com.developers.sprintsync.presentation.workouts_stats.chart.data.processing.WeeklyChartDataPreparer
import com.developers.sprintsync.presentation.workouts_stats.data.ChartDataUpdateEvent
import com.developers.sprintsync.presentation.workouts_stats.data.DateRange
import com.developers.sprintsync.presentation.workouts_stats.util.TimeWindowTrackFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutsStatisticsViewModel
@Inject
constructor(
    private val timeWindowTrackFilter: TimeWindowTrackFilter,
    private val getTracksFlowUseCase: GetTracksFlowUseCase,
    private val chartDataPreparer: WeeklyChartDataPreparer,
    private val workoutsStatsCreator: WorkoutsStatsCreator,
    private val workoutsStatsUiModelFormatter: WorkoutsStatsUiModelFormatter
) : ViewModel() {
    private val _chartDataUpdateEvent = MutableStateFlow<ChartDataUpdateEvent?>(null)
    val chartDataUpdate get() = _chartDataUpdateEvent.asStateFlow().filterNotNull()

    private val tracksState = MutableStateFlow<List<Track>>(emptyList())

    private var _dateRange: MutableStateFlow<DateRange> = MutableStateFlow(DateRange.EMPTY)
    val dateRange get() = _dateRange.asStateFlow()

    private var _selectedMetric: MutableStateFlow<Metric> = MutableStateFlow(Metric.DISTANCE)
    val selectedMetric get() = _selectedMetric.asStateFlow()

    private var _chartConfiguration: MutableStateFlow<ChartConfigurationType> =
        MutableStateFlow(ChartConfigurationType.WEEKLY)
    val chartConfig get() = _chartConfiguration.asStateFlow()

    private var _workoutsWeekStats: MutableStateFlow<WorkoutsStatsUiModel> =
        MutableStateFlow(WorkoutsStatsUiModel.EMPTY)
    val workoutsWeekStats get() = _workoutsWeekStats.asStateFlow()

    private val chartDataSet =
        MutableStateFlow(ChartDataSet.EMPTY)

    init {
        initSelectedMetricListener()
        initChartDataSetListener()
        initTracksFlowListener()
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
        val stats = workoutsStatsCreator.create(filteredTracks)
        val uiStats = workoutsStatsUiModelFormatter.format(stats)
        _workoutsWeekStats.update { uiStats }

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
                tracksState.update { tracks }

                chartDataSet.update {
                    chartDataPreparer.prepareChartSet(tracks, WeekDay.MONDAY)
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
