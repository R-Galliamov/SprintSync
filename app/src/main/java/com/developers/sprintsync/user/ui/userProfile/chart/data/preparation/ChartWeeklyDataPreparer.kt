package com.developers.sprintsync.user.ui.userProfile.chart.data.preparation

import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.chart.chartData.ChartDataSet
import com.developers.sprintsync.user.model.chart.chartData.WeekDay

// TODO : Sometimes launched before fragment is open
class ChartWeeklyDataPreparer {
    private val dailyMetricsAggregator = DailyMetricsAggregator()
    private val chartPreparationHelper = ChartPreparationHelper()
    private val chartDataSetCreator = ChartDataSetCreator(chartPreparationHelper)

    fun prepareChartSet(
        tracks: List<Track>,
        startDay: WeekDay,
    ): ChartDataSet {
        val goalValue = 1000.0f

        val firstTimestamp = chartPreparationHelper.findEarliestTimestamp(tracks) ?: return ChartDataSet.EMPTY
        val firstDataIndex = chartPreparationHelper.calculateWeekDayIndexFromTimestamp(firstTimestamp)
        val startIndex = startDay.index
        val preparedDataPoints = chartPreparationHelper.prepareChartDataPoints(firstDataIndex, startIndex, goalValue)
        val timestampMetrics = dailyMetricsAggregator.calculateMetricsForEachTrackingDay(tracks)
        return chartDataSetCreator.createDataSet(timestampMetrics, preparedDataPoints)
    }
}
