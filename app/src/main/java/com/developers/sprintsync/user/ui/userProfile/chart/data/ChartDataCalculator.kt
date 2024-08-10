package com.developers.sprintsync.user.ui.userProfile.chart.data

import com.developers.sprintsync.user.model.chart.DailyDataPoint

/**
 * A utility class for performing calculations on chart data.
 */
class ChartDataCalculator {
    /**
     * Calculates the maximum value between the goals and actual values in a list of [DailyDataPoint] objects.
     *
     * @param list The list of daily data points.
     * @return The maximum value found.
     */
    fun calculateMaxOfGoalAndActualValue(list: List<DailyDataPoint>): Float {
        val maxGoal = calculateMaxGoal(list)
        val maxValue = calculateMaxActualValue(list)
        return maxOf(maxGoal, maxValue)
    }

    /**
     * Calculates the maximum goal value from a list of [DailyDataPoint] objects.
     *
     * @param data The list of daily data points.
     * @return The maximum goal value or a default value if the list is empty.
     */
    fun calculateMaxGoal(data: List<DailyDataPoint>): Float = data.maxOfOrNull { it.goal } ?: DEFAULT_VALUE

    fun calculateLastGoal(data: List<DailyDataPoint>): Float = data.last().goal

    /**
     * Calculates the maximum actual value from a list of [DailyDataPoint] objects, considering only present data points.
     *
     * @param data The list of daily data points.
     * @return The maximum actual value or a default value if no present data points are found.
     */
    private fun calculateMaxActualValue(data: List<DailyDataPoint>): Float =
        data.filterIsInstance<DailyDataPoint.Present>().maxOfOrNull {
            it.actualValue
        } ?: DEFAULT_VALUE

    companion object {
        private const val DEFAULT_VALUE = 0f
    }
}
