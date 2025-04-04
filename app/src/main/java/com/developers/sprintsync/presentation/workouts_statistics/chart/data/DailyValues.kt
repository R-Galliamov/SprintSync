package com.developers.sprintsync.presentation.workouts_statistics.chart.data

sealed class DailyValues(
    open val goal: Float,
) {
    data class Present(
        override val goal: Float,
        val actualValue: Float,
    ) : DailyValues(goal) {
        companion object {
            val EMPTY = Present(0f, 0f)
        }
    }

    data class Missing(
        override val goal: Float,
    ) : DailyValues(goal)
}

