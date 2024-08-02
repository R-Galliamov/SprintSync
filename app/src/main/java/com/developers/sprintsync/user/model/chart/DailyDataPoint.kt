package com.developers.sprintsync.user.model.chart

sealed class DailyDataPoint(
    open val weekDay: WeekDay,
    open val goal: Float,
) {
    data class Present(
        override val weekDay: WeekDay,
        override val goal: Float,
        val value: Float,
    ) : DailyDataPoint(weekDay, goal)

    data class Missing(
        override val weekDay: WeekDay,
        override val goal: Float,
    ) : DailyDataPoint(weekDay, goal)
}
