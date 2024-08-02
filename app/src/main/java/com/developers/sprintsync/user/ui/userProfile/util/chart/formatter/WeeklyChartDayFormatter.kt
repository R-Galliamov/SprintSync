package com.developers.sprintsync.user.ui.userProfile.util.chart.formatter

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DateFormatSymbols

class WeeklyChartDayFormatter : ValueFormatter() {
    private val dayNames =
        DateFormatSymbols.getInstance().shortWeekdays

    override fun getAxisLabel(
        value: Float,
        axis: AxisBase?,
    ): String {
        val dayIndex = calculateAdjustedDayIndex(value.toInt())
        return getDayName(dayIndex)
    }

    private fun calculateAdjustedDayIndex(dayIndex: Int): Int =
        (
            dayIndex + DayIndexAdjustment.ARRAY_OFFSET.value + DayIndexAdjustment.DAY_SHIFT.value
        ) % DAYS_IN_WEEK

    private fun getDayName(adjustedIndex: Int): String =
        if (adjustedIndex == FIRST_DAY_INDEX) dayNames[DAYS_IN_WEEK] else dayNames[adjustedIndex]

    private enum class DayIndexAdjustment(
        val value: Int,
    ) {
        ARRAY_OFFSET(1), // Account for dayNames array starting at index 1
        DAY_SHIFT(1), // Shift days to align with user's start day
    }

    companion object {
        private const val DAYS_IN_WEEK = 7
        private const val FIRST_DAY_INDEX = 0
    }
}
