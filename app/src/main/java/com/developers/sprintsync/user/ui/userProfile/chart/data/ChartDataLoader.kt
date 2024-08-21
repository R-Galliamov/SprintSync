package com.developers.sprintsync.user.ui.userProfile.chart.data

import com.developers.sprintsync.tracking.dataStorage.repository.track.useCase.GetAllTracksUseCase
import com.developers.sprintsync.user.model.DailyGoal
import com.developers.sprintsync.user.model.chart.chartData.ChartDataSet
import com.developers.sprintsync.user.model.chart.chartData.WeekDay
import com.developers.sprintsync.user.ui.userProfile.chart.data.preparation.ChartWeeklyDataPreparer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChartDataLoader
    @Inject
    constructor(
        private val getAllTracksUseCase: GetAllTracksUseCase,
    ) {
        private var _chartDataSet = MutableStateFlow(ChartDataSet.EMPTY)
        val chartDataSet get() = _chartDataSet

        init {
            initCollector()
        }

        private fun initCollector() {
            val tracks = TrackTestContainer().tracks
            val goals: List<DailyGoal> = TrackTestContainer().goals

            CoroutineScope(Dispatchers.IO).launch {
                tracks.collect { trackList ->
                    if (trackList.isEmpty()) return@collect
                    val chartWeeklyDataPreparer = ChartWeeklyDataPreparer()
                    val dataSet = chartWeeklyDataPreparer.prepareChartSet(trackList, goals, WeekDay.MONDAY)
                    _chartDataSet.update { dataSet }
                }
            }
        }
    }
