package com.developers.sprintsync.user.ui.userProfile.chart.data

import com.developers.sprintsync.tracking.dataStorage.repository.track.useCase.GetAllTracksUseCase
import com.developers.sprintsync.user.model.chart.chartData.ChartDataSet
import com.developers.sprintsync.user.model.chart.chartData.WeekDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChartDataLoader
    @Inject
    constructor(
        private val getAllTracksUseCase: GetAllTracksUseCase,
    ) {
        var chartDataSet: ChartDataSet = ChartDataSet.EMPTY

        init {
            initCollector()
        }

        private fun initCollector() {
            val tracks = TrackTestContainer().tracks

            CoroutineScope(Dispatchers.IO).launch {
                tracks.collect { trackList ->
                    if (trackList.isEmpty()) return@collect
                    val chartWeeklyDataPreparer = ChartWeeklyDataPreparer()
                    val set = chartWeeklyDataPreparer.transformDataToChartSet(trackList, WeekDay.TUESDAY)
                    chartDataSet = set
                }
            }
        }
    }
