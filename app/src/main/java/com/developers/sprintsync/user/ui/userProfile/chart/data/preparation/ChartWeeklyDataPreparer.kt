package com.developers.sprintsync.user.ui.userProfile.chart.data.preparation

import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.goal.DailyGoal
import com.developers.sprintsync.user.model.chart.chartData.ChartDataSet
import com.developers.sprintsync.user.model.chart.chartData.WeekDay
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
