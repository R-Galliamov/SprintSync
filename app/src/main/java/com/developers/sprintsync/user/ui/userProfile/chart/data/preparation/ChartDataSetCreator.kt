package com.developers.sprintsync.user.ui.userProfile.chart.data.preparation

import com.developers.sprintsync.user.model.chart.chartData.ChartDataSet
import com.developers.sprintsync.user.model.chart.chartData.DailyValues
import com.developers.sprintsync.user.model.chart.chartData.IndexedDailyValues
import com.developers.sprintsync.user.model.chart.chartData.Metric
import com.developers.sprintsync.user.model.chart.chartData.MetricsMap
import com.developers.sprintsync.user.model.chart.chartData.TimestampMetricsMap
import com.developers.sprintsync.user.model.chart.chartData.util.time.TimeUtils

class ChartDataSetCreator(
    private val preparationHelper: ChartPreparationHelper,
    private val goalsProvider: DailyGoalsProvider,
) {
    fun createDataSet(
        timestampMetrics: TimestampMetricsMap,
        indexedDailyValues: IndexedDailyValues,
    ): ChartDataSet {
        val minKey = timestampMetrics.keys.min()
        val firstTimestamp = TimeUtils.shiftTimestampByDays(minKey, -indexedDailyValues.size)

        val earliestDayTimestamp = timestampMetrics.keys.min()
        val latestDayTimestamp = timestampMetrics.keys.max()

        var currentDayTimestamp = earliestDayTimestamp
        var dayIndex = indexedDailyValues.size

        var data = mutableMapOf<Metric, MutableMap<Int, DailyValues>>()

        indexedDailyValues.forEach { (day, dailyValues) ->
            Metric.entries.forEach { metric ->
                data.getOrPut(metric) { mutableMapOf() }[day] = dailyValues
            }
        }

        while (currentDayTimestamp <= latestDayTimestamp) {
            val dayValues: MetricsMap? = timestampMetrics[currentDayTimestamp]
            val goalValues: MetricsMap = goalsProvider.getGoalsForTimestamp(currentDayTimestamp)

            dayValues?.let { dayMetrics ->
                dayMetrics.forEach { (metric, value) ->
                    val goal = goalValues.getOrDefault(metric, 0f)

                    val indexedDayValues = data.getOrPut(metric) { mutableMapOf() }
                    val currentDayValue =
                        indexedDayValues.getOrDefault(
                            dayIndex,
                            DailyValues.Present(goal = goal, actualValue = value),
                        )
                    data.getOrPut(metric) { mutableMapOf() }[dayIndex] = currentDayValue
                }
            } ?: run {
                data.forEach { (metric, indexedValues) ->
                    val goal = goalValues.getOrDefault(metric, 0f)
                    indexedValues[dayIndex] = DailyValues.Missing(goal)
                }
            }
            currentDayTimestamp = TimeUtils.shiftTimestampByDays(currentDayTimestamp, 1)
            dayIndex++
        }

        data = padDataToRange(data, latestDayTimestamp)

        return ChartDataSet(firstTimestamp, data)
    }

    private fun padDataToRange(
        data: MutableMap<Metric, MutableMap<Int, DailyValues>>,
        latestDayTimestamp: Long,
    ): MutableMap<Metric, MutableMap<Int, DailyValues>> {
        val goals = goalsProvider.getGoalsForTimestamp(latestDayTimestamp)

        data.forEach { (metric, dataPoints) ->
            val goal = goals[metric] ?: 0f
            data[metric] = preparationHelper.padIndexedValuesToRange(dataPoints, goal).toMutableMap()
        }
        return data
    }
}
