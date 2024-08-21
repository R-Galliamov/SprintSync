package com.developers.sprintsync.user.ui.userProfile.chart.data.preparation

import com.developers.sprintsync.global.util.extension.isMultipleOf
import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.DailyGoal
import com.developers.sprintsync.user.model.chart.chartData.DailyValues
import java.util.Calendar

class ChartPreparationHelper {
    fun findEarliestTimestamp(tracks: List<Track>): Long? = tracks.minByOrNull { it.timestamp }.takeIf { it != null }?.timestamp

    fun calculateWeekDayIndexFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        var weekDayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 2
        if (weekDayIndex < 0) weekDayIndex = 6
        return weekDayIndex
    }

    fun prepareIndexedValues(
        firstDataIndex: Int,
        startIndex: Int,
        goals: List<DailyGoal>,
    ): List<DailyValues> {
        val goalValue = goals.firstOrNull()?.value ?: 0f

        val dailyValues = mutableListOf<DailyValues>()
        if (hasMissingStartData(firstDataIndex, startIndex)) {
            val shiftDays = calculateShiftDays(firstDataIndex, startIndex)
            repeat(shiftDays) {
                dailyValues.add(DailyValues.Missing(goalValue))
            }
        }
        return dailyValues
    }

    fun padIndexedValuesToRange(
        dailyValuesList: MutableList<DailyValues>,
        goal: Float,
    ): List<DailyValues> {
        while (!dailyValuesList.size.isMultipleOf(DAYS_IN_WEEK)) {
            dailyValuesList.add(DailyValues.Missing(goal))
        }
        return dailyValuesList
    }

    private fun hasMissingStartData(
        firstDataIndex: Int,
        startIndex: Int,
    ) = firstDataIndex != startIndex

    private fun calculateShiftDays(
        firstDataIndex: Int,
        startIndex: Int,
    ) = DAYS_IN_WEEK - ((startIndex - firstDataIndex + DAYS_IN_WEEK) % DAYS_IN_WEEK)

    companion object {
        private const val DAYS_IN_WEEK = 7
    }
}
