package com.developers.sprintsync.user.ui.userProfile.chart.data

import com.developers.sprintsync.user.model.chart.chartData.DailyDataPoint

/**
 * A utility class for performing calculations on chart data.
 */
class ChartDataCalculator {
    fun calculateMaxOfGoalAndActualValue(list: ChartData): Float {
        val maxGoal = calculateMaxGoal(list)
        val maxValue = calculateMaxActualValue(list)
        return maxOf(maxGoal, maxValue)
    }

    fun calculateMaxGoal(data: ChartData): Float = data.maxOfOrNull { it.goal } ?: DEFAULT_VALUE

    fun calculateLastGoal(data: ChartData): Float = data.last().goal

    private fun calculateMaxActualValue(data: ChartData): Float =
        data.filterIsInstance<DailyDataPoint.Present>().maxOfOrNull {
            it.actualValue
        } ?: DEFAULT_VALUE

    companion object {
        private const val DEFAULT_VALUE = 0f
    }
}
