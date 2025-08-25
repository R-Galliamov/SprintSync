package com.developers.sprintsync.presentation.workouts_stats.chart.data.processing

import android.util.Log
import com.developers.sprintsync.core.util.extension.setOrAdd
import com.developers.sprintsync.core.util.timestamp.TimestampUtils
import com.developers.sprintsync.domain.workouts_plan.model.Metric
import com.developers.sprintsync.presentation.workouts_stats.chart.data.ChartDataSet
import com.developers.sprintsync.presentation.workouts_stats.chart.data.DailyValues
import com.developers.sprintsync.presentation.workouts_stats.chart.data.MetricsMap
import com.developers.sprintsync.presentation.workouts_stats.chart.data.TimestampMetricsMap

class ChartDataProcessor(
    private val preparationHelper: ChartDataPreparationHelper,
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

            dayValues?.let { dayMetrics ->
                dayMetrics.forEach { (metric, value) ->

                    val dailyValuesForMetric = data.getOrPut(metric) { mutableListOf() }

                    val currentDayValues =
                        dailyValuesForMetric.getOrNull(dayIndex) ?: DailyValues.Present(
                            actualValue = value,
                        )
                    val dataDailyValues = data.getOrPut(metric) { mutableListOf() }
                    dataDailyValues.setOrAdd(dayIndex, currentDayValues)
                }
            } ?: run {
                data.forEach { (metric, indexedValues) ->
                    indexedValues.add(DailyValues.Missing)
                }
            }
            currentDayTimestamp = TimestampUtils.addDaysToTimestamp(currentDayTimestamp, 1)
            dayIndex++
        }

        data = fillDataToRange(data)
        Log.d(TAG, "createDataSet: $data")
        return ChartDataSet(firstTimestamp, data)
    }

    private fun fillDataToRange(
        data: MutableMap<Metric, MutableList<DailyValues>>,
    ): MutableMap<Metric, MutableList<DailyValues>> {
        data.forEach { (metric, dailyValuesList) ->
            val paddedDailyValues = preparationHelper.fillDailyValuesToRange(dailyValuesList)
            data[metric] = paddedDailyValues.toMutableList()
        }
        return data
    }

    companion object {
        private const val TAG = "My Stack: ChartDataSetCreator"
    }
}
