package com.developers.sprintsync.user.model.chart

sealed class DailyDataPoint(
    open val dayIndex: Int,
    open val goal: Float,
) {
    data class Present(
        override val dayIndex: Int,
        override val goal: Float,
        val actualValue: Float,
    ) : DailyDataPoint(dayIndex, goal)

    data class Missing(
        override val dayIndex: Int,
        override val goal: Float,
    ) : DailyDataPoint(dayIndex, goal)
}
