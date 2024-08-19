package com.developers.sprintsync.user.ui.userProfile.chart.data

import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.chart.chartData.WeekDay
import java.util.Calendar

enum class Metric {
    DISTANCE,
    DURATION,
}

data class ChartDataSet(
    val referenceTimestamp: Long,
    val data: Map<Metric, ChartDataPoints>,
) {
    companion object {
        val EMPTY = ChartDataSet(0, emptyMap())
    }
}

typealias ChartDataPoints = Map<Int, DailyValues>

sealed class DailyValues(
    open val goal: Float,
) {
    data class Present(
        override val goal: Float,
        val actualValue: Float,
    ) : DailyValues(goal)

    data class Missing(
        override val goal: Float,
    ) : DailyValues(goal)
}

// TODO : Sometimes launched before fragment is open
class ChartWeeklyDataPreparer {
    fun transformDataToChartSet(
        tracks: List<Track>,
        startDay: WeekDay,
    ): ChartDataSet {
        val goalValue = 1000.0f

        val firstTimestamp = findEarliestTimestamp(tracks) ?: return ChartDataSet.EMPTY
        val firstDataIndex = calculateWeekDayIndexFromTimestamp(firstTimestamp)
        val startIndex = startDay.index
        val preparedDataPoints = prepareChartDataPoints(firstDataIndex, startIndex, goalValue)

        val timestampMetrics: Map<Long, Map<Metric, Float>> = calculateMetricsForEachTrackingDay(tracks)

        return createDataSet(timestampMetrics, preparedDataPoints)
    }

    private fun createDataSet(
        timestampMetrics: Map<Long, Map<Metric, Float>>,
        chartDataPoints: ChartDataPoints,
    ): ChartDataSet {
        val minKey = timestampMetrics.keys.min()
        val firstTimestamp = shiftTimestamp(minKey, -chartDataPoints.size)

        // Find the earliest and latest days to cover the full range
        val earliestDayTimestamp = timestampMetrics.keys.min()
        val latestDayTimestamp = timestampMetrics.keys.max()

        var currentDayTimestamp = earliestDayTimestamp
        var dayIndex = chartDataPoints.size

        val goalValue = 1000.0f // TODO: Replace with actual goal value

        val data = mutableMapOf<Metric, MutableMap<Int, DailyValues>>()

        chartDataPoints.forEach { (day, dailyValues) ->
            Metric.entries.forEach { metric ->
                data.getOrPut(metric) { mutableMapOf() }[day] = dailyValues
            }
        }

        while (currentDayTimestamp <= latestDayTimestamp) {
            val dayValues: Map<Metric, Float>? = timestampMetrics[currentDayTimestamp]
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
            currentDayTimestamp = shiftTimestamp(currentDayTimestamp, 1)
            dayIndex++
        }

        data.forEach { (_, dataPoints) ->
            while (dataPoints.size % 7 != 0) {
                var lastDayIndex = dataPoints.keys.max()
                lastDayIndex++
                dataPoints[lastDayIndex] = DailyValues.Missing(goalValue)
            }
        }

        return ChartDataSet(firstTimestamp, data)
    }

    private fun findEarliestTimestamp(tracks: List<Track>): Long? = tracks.minByOrNull { it.timestamp }.takeIf { it != null }?.timestamp

    private fun calculateWeekDayIndexFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        var weekDayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 2
        if (weekDayIndex < 0) weekDayIndex = 6
        return weekDayIndex
    }

    private fun prepareChartDataPoints(
        firstDataIndex: Int,
        startIndex: Int,
        goalValue: Float,
    ): ChartDataPoints {
        val dataPoints = mutableMapOf<Int, DailyValues>()
        if (hasMissingStartData(firstDataIndex, startIndex)) {
            val shiftDays = calculateShiftDays(firstDataIndex, startIndex)
            repeat(shiftDays) { dayIndex ->
                dataPoints[dayIndex] = DailyValues.Missing(goalValue)
            }
        }
        return dataPoints
    }

    private fun shiftTimestamp(
        timestamp: Long,
        shiftDays: Int,
    ): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        calendar.add(Calendar.DAY_OF_YEAR, shiftDays)
        return calendar.timeInMillis
    }

    private fun hasMissingStartData(
        firstDataIndex: Int,
        startIndex: Int,
    ) = firstDataIndex != startIndex

    private fun calculateShiftDays(
        firstDataIndex: Int,
        startIndex: Int,
    ) = 7 - ((startIndex - firstDataIndex + 7) % 7)

    private fun setTimeToStartOfDay(timestamp: Long): Long {
        val calendar =
            Calendar.getInstance().apply { timeInMillis = timestamp }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun calculateMetricsForEachTrackingDay(tracks: List<Track>): Map<Long, Map<Metric, Float>> {
        val dailyValuesMap = mutableMapOf<Long, Map<Metric, Float>>()
        tracks.forEach { track ->
            val dayTimestampKey = setTimeToStartOfDay(track.timestamp)
            val valuesMap: Map<Metric, Float> = dailyValuesMap[dayTimestampKey] ?: emptyMap()
            val updatedValuesMap = valuesMap.toMutableMap()
            updatedValuesMap[Metric.DISTANCE] =
                updatedValuesMap.getOrDefault(Metric.DISTANCE, 0f) + track.distanceMeters
            updatedValuesMap[Metric.DURATION] =
                updatedValuesMap.getOrDefault(Metric.DURATION, 0f) + track.durationMillis
            dailyValuesMap[dayTimestampKey] = updatedValuesMap
        }
        return dailyValuesMap
    }
}
