package com.developers.sprintsync.statistics.ui.statistics.chart.data.preparation

import android.util.Log
import com.developers.sprintsync.global.util.extension.setOrAdd
import com.developers.sprintsync.statistics.model.chart.chartData.ChartDataSet
import com.developers.sprintsync.statistics.model.chart.chartData.DailyValues
import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.statistics.model.chart.chartData.MetricsMap
import com.developers.sprintsync.statistics.model.chart.chartData.TimestampMetricsMap
import com.developers.sprintsync.statistics.model.chart.chartData.util.time.TimeUtils

class ChartDataSetCreator(
    private val preparationHelper: ChartPreparationHelper,
    private val goalsProvider: DailyGoalsProvider,
) {
    fun createDataSet(
        timestampMetrics: TimestampMetricsMap,
        dailyValuesList: List<DailyValues>,
    ): ChartDataSet {
        val minKey = timestampMetrics.keys.min()
        val firstTimestamp = TimeUtils.shiftTimestampByDays(minKey, -dailyValuesList.size)

        val earliestDayTimestamp = timestampMetrics.keys.min()
        val latestDayTimestamp = timestampMetrics.keys.max()

        var currentDayTimestamp = earliestDayTimestamp
        var dayIndex = dailyValuesList.size

        var data = mutableMapOf<Metric, MutableList<DailyValues>>()

        dailyValuesList.forEach { dailyValues ->
            Metric.entries.forEach { metric ->
                data.getOrPut(metric) { mutableListOf() }.add(dailyValues)
            }
        }

        while (currentDayTimestamp <= latestDayTimestamp) {
            val dayValues: MetricsMap? = timestampMetrics[currentDayTimestamp]
            val goalValues: MetricsMap = goalsProvider.getGoalsForTimestamp(currentDayTimestamp)

            dayValues?.let { dayMetrics ->
                dayMetrics.forEach { (metric, value) ->
                    val goal = goalValues.getOrDefault(metric, 0f)

                    val dailyValuesForMetric = data.getOrPut(metric) { mutableListOf() }

                    val currentDayValues =
                        dailyValuesForMetric.getOrNull(dayIndex) ?: DailyValues.Present(
                            goal = goal,
                            actualValue = value,
                        )
                    val dataDailyValues = data.getOrPut(metric) { mutableListOf() }
                    dataDailyValues.setOrAdd(dayIndex, currentDayValues)
                }
            } ?: run {
                data.forEach { (metric, indexedValues) ->
                    val goal = goalValues.getOrDefault(metric, 0f)
                    indexedValues.add(DailyValues.Missing(goal))
                }
            }
            currentDayTimestamp = TimeUtils.shiftTimestampByDays(currentDayTimestamp, 1)
            dayIndex++
        }

        data = padDataToRange(data, latestDayTimestamp)
        Log.d(TAG, "createDataSet: $data")
        return ChartDataSet(firstTimestamp, data)
    }

    private fun padDataToRange(
        data: MutableMap<Metric, MutableList<DailyValues>>,
        latestDayTimestamp: Long,
    ): MutableMap<Metric, MutableList<DailyValues>> {
        val goals = goalsProvider.getGoalsForTimestamp(latestDayTimestamp)

        data.forEach { (metric, dailyValuesList) ->
            val goal = goals[metric] ?: 0f
            val paddedDailyValues = preparationHelper.padIndexedValuesToRange(dailyValuesList, goal)
            data[metric] = paddedDailyValues.toMutableList()
        }
        return data
    }

    companion object {
        private const val TAG = "My Stack: ChartDataSetCreator"
    }
}
