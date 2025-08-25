package com.developers.sprintsync.presentation.workouts_stats.chart.data.processing

import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.presentation.workouts_stats.chart.data.ChartDataSet
import com.developers.sprintsync.presentation.workouts_stats.chart.data.WeekDay
import javax.inject.Inject

class WeeklyChartDataPreparer
    @Inject
    constructor() {
        private val metricsAggregator = MetricsAggregator()
        private val chartDataPreparationHelper = ChartDataPreparationHelper()

        fun prepareChartSet(
            tracks: List<Track>,
            startDay: WeekDay,
        ): ChartDataSet {
            val chartDataProcessor = ChartDataProcessor(chartDataPreparationHelper)

            val firstTimestamp = chartDataPreparationHelper.getEarliestTimestamp(tracks) ?: return ChartDataSet.EMPTY
            val firstDataIndex = chartDataPreparationHelper.getWeekDayIndexFromTimestamp(firstTimestamp)
            val startIndex = startDay.index
            val preparedIndexedValues = chartDataPreparationHelper.getDailyValues(firstDataIndex, startIndex)
            val timestampMetrics = metricsAggregator.calculateMetricsForEachTrackingDay(tracks)
            return chartDataProcessor.generateChartDataSet(timestampMetrics, preparedIndexedValues)
        }

        companion object {
            private const val TAG = "My Stack: ChartWeeklyDataPreparer"
        }
    }
