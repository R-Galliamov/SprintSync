package com.developers.sprintsync.user.ui.userProfile.chart.data.preparation

import com.developers.sprintsync.global.util.extension.isMultipleOf
import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.chart.chartData.DailyValues
import com.developers.sprintsync.user.model.chart.chartData.IndexedDailyValues
import java.util.Calendar

class ChartPreparationHelper {
    fun findEarliestTimestamp(tracks: List<Track>): Long? = tracks.minByOrNull { it.timestamp }.takeIf { it != null }?.timestamp

    fun calculateWeekDayIndexFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        var weekDayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 2
        if (weekDayIndex < 0) weekDayIndex = 6
        return weekDayIndex
    }

    fun prepareChartDataPoints(
        firstDataIndex: Int,
        startIndex: Int,
        goalValue: Float,
    ): IndexedDailyValues {
        val dataPoints = mutableMapOf<Int, DailyValues>()
        if (hasMissingStartData(firstDataIndex, startIndex)) {
            val shiftDays = calculateShiftDays(firstDataIndex, startIndex)
            repeat(shiftDays) { dayIndex ->
                dataPoints[dayIndex] = DailyValues.Missing(goalValue)
            }
        }
        return dataPoints
    }

    fun padDataPointsToRange(
        dataPoints: MutableMap<Int, DailyValues>,
        goalValue: Float,
    ): IndexedDailyValues {
        while (!dataPoints.size.isMultipleOf(DAYS_IN_WEEK)) {
            val lastDayIndex = dataPoints.keys.maxOrNull() ?: 0 // Handle the case of an empty map
            dataPoints[lastDayIndex.inc()] = DailyValues.Missing(goalValue)
        }
        return dataPoints
    }

    private fun hasMissingStartData(
        firstDataIndex: Int,
        startIndex: Int,
    ) = firstDataIndex != startIndex

    private fun calculateShiftDays(
        firstDataIndex: Int,
        startIndex: Int,
    ) = DAYS_IN_WEEK - ((startIndex - firstDataIndex + DAYS_IN_WEEK) % DAYS_IN_WEEK)

    fun shiftTimestamp(
        timestamp: Long,
        shiftDays: Int,
    ): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        calendar.add(Calendar.DAY_OF_YEAR, shiftDays)
        return calendar.timeInMillis
    }

    companion object {
        private const val DAYS_IN_WEEK = 7
    }
}
