package com.developers.sprintsync.statistics.domain.chart.data.processing

import com.developers.sprintsync.statistics.domain.chart.data.ChartDataSet
import com.developers.sprintsync.statistics.domain.chart.data.WeekDay
import com.developers.sprintsync.core.components.goal.data.model.DailyGoal
import com.developers.sprintsync.core.components.track.data.model.Track
import javax.inject.Inject

class WeeklyChartDataPreparer
    @Inject
    constructor() {
        private val metricsAggregator = MetricsAggregator()
        private val chartDataPreparationHelper = ChartDataPreparationHelper()

        fun prepareChartSet(
            tracks: List<Track>,
            goals: List<DailyGoal>,
            startDay: WeekDay,
        ): ChartDataSet {
            val goalsProvider = DailyGoalProvider(goals)
            val chartDataProcessor = ChartDataProcessor(chartDataPreparationHelper, goalsProvider)

            val firstTimestamp = chartDataPreparationHelper.getEarliestTimestamp(tracks) ?: return ChartDataSet.EMPTY
            val firstDataIndex = chartDataPreparationHelper.getWeekDayIndexFromTimestamp(firstTimestamp)
            val startIndex = startDay.index
            val preparedIndexedValues = chartDataPreparationHelper.getDailyValues(firstDataIndex, startIndex, goals)
            val timestampMetrics = metricsAggregator.calculateMetricsForEachTrackingDay(tracks)
            return chartDataProcessor.generateChartDataSet(timestampMetrics, preparedIndexedValues)
        }

        companion object {
            private const val TAG = "My Stack: ChartWeeklyDataPreparer"
        }
    }