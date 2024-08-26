package com.developers.sprintsync.user.ui.userProfile.chart.data

import com.developers.sprintsync.user.model.chart.chartData.DailyValues

/**
 * A utility class for performing calculations on chart data.
 */
class ChartValuesCalculator {
    fun calculateMaxOfGoalAndActualValue(dailyValues: List<DailyValues>): Float {
        val maxGoal = calculateMaxGoal(dailyValues)
        val maxValue = calculateMaxActualValue(dailyValues)
        return maxOf(maxGoal, maxValue)
    }

    fun calculateMaxGoal(data: List<DailyValues>): Float = data.maxOfOrNull { it.goal } ?: DEFAULT_VALUE

    fun calculateLastGoal(data: List<DailyValues>): Float = data.last().goal

    fun calculateMinGoal(data: List<DailyValues>): Float = data.minOfOrNull { it.goal } ?: DEFAULT_VALUE

    private fun calculateMaxActualValue(data: List<DailyValues>): Float =
        data.filterIsInstance<DailyValues.Present>().maxOfOrNull {
            it.actualValue
        } ?: DEFAULT_VALUE

    companion object {
        private const val DEFAULT_VALUE = 0f
    }
}
