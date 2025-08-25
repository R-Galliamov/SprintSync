package com.developers.sprintsync.presentation.workouts_stats.chart.data

sealed class DailyValues {
    data class Present(
        val actualValue: Float,
    ) : DailyValues() {
        companion object {
            val EMPTY = Present(0f)
        }
    }

    data object Missing : DailyValues()
}

fun List<DailyValues>.getActualValues(): List<Float> =
    this.filterIsInstance<DailyValues.Present>().map { it.actualValue }

