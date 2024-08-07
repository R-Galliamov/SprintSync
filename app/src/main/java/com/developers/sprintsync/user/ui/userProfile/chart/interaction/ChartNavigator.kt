package com.developers.sprintsync.user.ui.userProfile.chart.interaction

import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.model.chart.WeeklyChartData

/**
 * A class for navigating between weeks of data in a chart.
 *
 * @param data The weekly chart data to navigate through.
 */
class ChartNavigator(
    private val data: WeeklyChartData,
) {
    private var _currentWeekIndex = 0
    val currentWeekIndex get() = _currentWeekIndex

    /**
     * Returns the data for the currently selected week.
     *
     * @return A list of [DailyDataPoint] objects representing the data for the current week.
     */
    fun getCurrentWeekData(): List<DailyDataPoint> {
        val startDayIndex = _currentWeekIndex * DAYS_PER_WEEK
        val endDayIndex = startDayIndex + DAYS_PER_WEEK
        return data.data.subList(startDayIndex, endDayIndex)
    }

    /**
     * Navigates to the previous or next week based on the provided direction.
     *
     * @param direction The direction to navigate, either [NavigationDirection.PREVIOUS] or [NavigationDirection.NEXT].
     */
    fun navigateWeek(direction: NavigationDirection) {
        when (direction) {
            NavigationDirection.PREVIOUS -> {
                if (currentWeekIndex > FIRST_WEEK_INDEX) {
                    _currentWeekIndex--
                }
            }

            NavigationDirection.NEXT -> {
                val maxWeekIndex = (data.data.size / DAYS_PER_WEEK) - INDEX_OFFSET
                if (currentWeekIndex < maxWeekIndex) {
                    _currentWeekIndex++
                }
            }
        }
    }

    /**
     * Represents the direction of navigation for the chart.
     */
    enum class NavigationDirection {
        PREVIOUS,
        NEXT,
    }

    companion object {
        private const val FIRST_WEEK_INDEX = 0
        private const val INDEX_OFFSET = 1
        private const val DAYS_PER_WEEK = 7
    }
}
