package com.developers.sprintsync.user.model.chart.chartData

sealed class DailyValues(
    open val goal: Float,
) {
    data class Present(
        override val goal: Float,
        val actualValue: Float,
    ) : DailyValues(goal)

    data class Missing(
        override val goal: Float,
    ) : DailyValues(goal)
}
