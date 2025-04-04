package com.developers.sprintsync.presentation.workouts_statistics.chart.data.processing

import com.developers.sprintsync.core.util.extension.isMultipleOf
import com.developers.sprintsync.presentation.workouts_statistics.chart.data.DailyValues
import com.developers.sprintsync.domain.goal.model.DailyGoal
import com.developers.sprintsync.domain.track.model.Track
import java.util.Calendar

class ChartDataPreparationHelper {
    fun getEarliestTimestamp(tracks: List<Track>): Long? = tracks.minByOrNull { it.timestamp }.takeIf { it != null }?.timestamp

    fun getWeekDayIndexFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        var weekDayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 2
        if (weekDayIndex < 0) weekDayIndex = 6
        return weekDayIndex
    }

    fun getDailyValues(
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

    fun fillDailyValuesToRange(
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
