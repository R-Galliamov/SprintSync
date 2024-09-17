package com.developers.sprintsync.statistics.ui.statistics.chart.data.preparation

import com.developers.sprintsync.statistics.model.chart.chartData.ChartDataSet
import com.developers.sprintsync.statistics.model.chart.chartData.WeekDay
import com.developers.sprintsync.statistics.model.goal.DailyGoal
import com.developers.sprintsync.tracking.session.model.track.Track
import javax.inject.Inject

class ChartWeeklyDataPreparer
    @Inject
    constructor() {
        private val dailyMetricsAggregator = DailyMetricsAggregator()
        private val chartPreparationHelper = ChartPreparationHelper()

        fun prepareChartSet(
            tracks: List<Track>,
            goals: List<DailyGoal>,
            startDay: WeekDay,
        ): ChartDataSet {
            val goalsProvider = DailyGoalsProvider(goals)
            val chartDataSetCreator = ChartDataSetCreator(chartPreparationHelper, goalsProvider)

            val firstTimestamp = chartPreparationHelper.findEarliestTimestamp(tracks) ?: return ChartDataSet.EMPTY
            val firstDataIndex = chartPreparationHelper.calculateWeekDayIndexFromTimestamp(firstTimestamp)
            val startIndex = startDay.index
            val preparedIndexedValues = chartPreparationHelper.prepareIndexedValues(firstDataIndex, startIndex, goals)
            val timestampMetrics = dailyMetricsAggregator.calculateMetricsForEachTrackingDay(tracks)
            return chartDataSetCreator.createDataSet(timestampMetrics, preparedIndexedValues)
        }

        companion object {
            private const val TAG = "My Stack: ChartWeeklyDataPreparer"
        }
    }
