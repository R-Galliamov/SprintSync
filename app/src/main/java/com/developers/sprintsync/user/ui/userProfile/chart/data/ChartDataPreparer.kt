package com.developers.sprintsync.user.ui.userProfile.chart.data

import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.chart.chartData.DailyDataPoint
import com.developers.sprintsync.user.model.chart.chartData.WeekDay
import java.util.Calendar

enum class Metric {
    DISTANCE,
    DURATION, // Add more metrics as needed
}

data class ChartDataSet(
    val referenceTimestamp: Long,
    val data: Map<Metric, ChartDataPoints>,
) {
    companion object {
        val EMPTY = ChartDataSet(0, emptyMap())
    }
}

typealias ChartDataPoints = Map<Int, List<DailyValues>>

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
class ChartDataPreparer {
    data class DayValues(
        val distance: Distance,
        val duration: Duration,
    ) {
        companion object {
            val EMPTY = DayValues(Distance(0f), Duration(0f))
        }
    }

    @JvmInline
    value class Duration(
        val value: Float,
    )

    @JvmInline
    value class Distance(
        val value: Float,
    )

    fun transformDataToChartSet(
        tracks: List<Track>,
        startDay: WeekDay,
    ): ChartDataSet {
        val goalValue = 1000.0f

        val firstTimestamp = findEarliestTimestamp(tracks) ?: return ChartDataSet.EMPTY
        val firstDataIndex = calculateWeekDayIndexFromTimestamp(firstTimestamp)
        val startIndex = startDay.index
        val preparedDataPoints = prepareDailyDataPoints(firstDataIndex, startIndex, goalValue)

        val dailyValuesMap = calculateDailyValues(tracks)

        return createDataSet(dailyValuesMap, preparedDataPoints)
    }

    private fun createDataSet(
        dailyValuesMap: Map<Long, DayValues>,
        dailyDataPoints: List<DailyDataPoint>,
    ): ChartDataSet {
        val minKey = dailyValuesMap.minBy { it.key }.key
        val firstTimestamp = shiftTimestamp(minKey, -dailyDataPoints.size)
        // Find the earliest and latest days to cover the full range
        val earliestDay = dailyValuesMap.keys.min()
        val latestDay = dailyValuesMap.keys.max()

        var currentDay = earliestDay
        var dayIndex = dailyDataPoints.size

        val goalValue = 1000.0f // TODO: Replace with actual goal value

        val dataPointsDistance = dailyDataPoints.toMutableList()
        val dataPointsDuration = dailyDataPoints.toMutableList()

        while (currentDay <= latestDay) {
            val dayValues = dailyValuesMap[currentDay]
            dayValues?.let {
                dataPointsDistance.add(DailyDataPoint.Present(dayIndex, goalValue, it.distance.value))
                dataPointsDuration.add(DailyDataPoint.Present(dayIndex, goalValue, it.duration.value))
            } ?: run {
                dataPointsDistance.add(DailyDataPoint.Missing(dayIndex, goalValue))
                dataPointsDuration.add(DailyDataPoint.Missing(dayIndex, goalValue))
            }
            // Move to the next day
            currentDay = shiftTimestamp(currentDay, 1)
            dayIndex++
        }

        while (dataPointsDistance.size % 7 != 0) {
            var lastDayIndex = dataPointsDistance.last().dayIndex
            lastDayIndex++
            dataPointsDistance.add(DailyDataPoint.Missing(lastDayIndex, goalValue))
            dataPointsDuration.add(DailyDataPoint.Missing(lastDayIndex, goalValue))
        }

        return ChartDataSet(
            firstTimestamp,
            dataPointsDuration,
            dataPointsDistance,
        )
    }

    private fun findEarliestTimestamp(tracks: List<Track>): Long? = tracks.minByOrNull { it.timestamp }.takeIf { it != null }?.timestamp

    private fun calculateWeekDayIndexFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        var weekDayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 2
        if (weekDayIndex < 0) weekDayIndex = 6
        return weekDayIndex
    }

    private fun prepareDailyDataPoints(
        firstDataIndex: Int,
        startIndex: Int,
        goalValue: Float,
    ): Map<Int, List<DailyDataPoint>> {
        val dailyDataPoints = mutableListOf<DailyDataPoint>()
        if (hasMissingStartData(firstDataIndex, startIndex)) {
            val shiftDays = calculateShiftDays(firstDataIndex, startIndex)
            repeat(shiftDays) { i ->
                dailyDataPoints.add(DailyDataPoint.Missing(i, goalValue))
            }
        }
        return dailyDataPoints
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

    private fun calculateDailyValues(tracks: List<Track>): Map<Long, DayValues> {
        val dailyValues = mutableMapOf<Long, DayValues>()
        tracks.forEach { track ->
            val key = setTimeToStartOfDay(track.timestamp)
            val currentData = (dailyValues[key] ?: DayValues.EMPTY).copy()
            dailyValues[key] =
                DayValues(
                    Distance(currentData.distance.value + track.distanceMeters),
                    Duration(currentData.duration.value + track.durationMillis),
                )
        }
        return dailyValues
    }
}
