package com.developers.sprintsync.user.ui.userProfile.chart.data.preparation

import com.developers.sprintsync.user.model.chart.chartData.ChartDataSet
import com.developers.sprintsync.user.model.chart.chartData.DailyValues
import com.developers.sprintsync.user.model.chart.chartData.IndexedDailyValues
import com.developers.sprintsync.user.model.chart.chartData.Metric
import com.developers.sprintsync.user.model.chart.chartData.MetricsMap
import com.developers.sprintsync.user.model.chart.chartData.TimestampMetricsMap

class ChartDataSetCreator(
    private val preparationHelper: ChartPreparationHelper,
) {
    fun createDataSet(
        timestampMetrics: TimestampMetricsMap,
        indexedDailyValues: IndexedDailyValues,
    ): ChartDataSet {
        val minKey = timestampMetrics.keys.min()
        val firstTimestamp = preparationHelper.shiftTimestamp(minKey, -indexedDailyValues.size)

        // Find the earliest and latest days to cover the full range
        val earliestDayTimestamp = timestampMetrics.keys.min()
        val latestDayTimestamp = timestampMetrics.keys.max()

        var currentDayTimestamp = earliestDayTimestamp
        var dayIndex = indexedDailyValues.size

        val goalValue = 3600000.0f // TODO: Replace with actual goal value

        val data = mutableMapOf<Metric, MutableMap<Int, DailyValues>>()

        indexedDailyValues.forEach { (day, dailyValues) ->
            Metric.entries.forEach { metric ->
                data.getOrPut(metric) { mutableMapOf() }[day] = dailyValues
            }
        }

        while (currentDayTimestamp <= latestDayTimestamp) {
            val dayValues: MetricsMap? = timestampMetrics[currentDayTimestamp]
            dayValues?.let { dayMetrics ->

                dayMetrics.forEach { (metric, value) ->
                    val indexedDayValues = data.getOrPut(metric) { mutableMapOf() }
                    val currentDayValue = indexedDayValues.getOrDefault(dayIndex, DailyValues.Present(goalValue, value))
                    data.getOrPut(metric) { mutableMapOf() }[dayIndex] = currentDayValue
                }
            } ?: run {
                data.forEach { (_, dataPoints) ->
                    dataPoints[dayIndex] = DailyValues.Missing(goalValue)
                }
            }
            currentDayTimestamp = preparationHelper.shiftTimestamp(currentDayTimestamp, 1)
            dayIndex++
        }

        data.forEach { (metric, dataPoints) ->
            data[metric] = preparationHelper.padDataPointsToRange(dataPoints, goalValue).toMutableMap()
        }

        return ChartDataSet(firstTimestamp, data)
    }
}
