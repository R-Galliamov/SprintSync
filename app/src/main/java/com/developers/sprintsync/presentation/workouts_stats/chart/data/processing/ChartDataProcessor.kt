package com.developers.sprintsync.presentation.workouts_stats.chart.data.processing

import android.util.Log
import com.developers.sprintsync.core.util.extension.setOrAdd
import com.developers.sprintsync.presentation.workouts_stats.chart.data.ChartDataSet
import com.developers.sprintsync.presentation.workouts_stats.chart.data.DailyValues
import com.developers.sprintsync.domain.workouts_plan.model.Metric
import com.developers.sprintsync.presentation.workouts_stats.chart.data.MetricsMap
import com.developers.sprintsync.presentation.workouts_stats.chart.data.TimestampMetricsMap
import com.developers.sprintsync.core.util.timestamp.TimestampUtils

class ChartDataProcessor(
    private val preparationHelper: ChartDataPreparationHelper,
    private val goalsProvider: DailyGoalProvider,
) {
    fun generateChartDataSet(
        timestampMetrics: TimestampMetricsMap,
        dailyValuesList: List<DailyValues>,
    ): ChartDataSet {
        val minKey = timestampMetrics.keys.min()
        val firstTimestamp = TimestampUtils.addDaysToTimestamp(minKey, -dailyValuesList.size)

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
            val goalValues: MetricsMap = goalsProvider.fetchGoalsForTimestamp(currentDayTimestamp)

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
            currentDayTimestamp = TimestampUtils.addDaysToTimestamp(currentDayTimestamp, 1)
            dayIndex++
        }

        data = fillDataToRange(data, latestDayTimestamp)
        Log.d(TAG, "createDataSet: $data")
        return ChartDataSet(firstTimestamp, data)
    }

    private fun fillDataToRange(
        data: MutableMap<Metric, MutableList<DailyValues>>,
        latestDayTimestamp: Long,
    ): MutableMap<Metric, MutableList<DailyValues>> {
        val goals = goalsProvider.fetchGoalsForTimestamp(latestDayTimestamp)

        data.forEach { (metric, dailyValuesList) ->
            val goal = goals[metric] ?: 0f
            val paddedDailyValues = preparationHelper.fillDailyValuesToRange(dailyValuesList, goal)
            data[metric] = paddedDailyValues.toMutableList()
        }
        return data
    }

    companion object {
        private const val TAG = "My Stack: ChartDataSetCreator"
    }
}
