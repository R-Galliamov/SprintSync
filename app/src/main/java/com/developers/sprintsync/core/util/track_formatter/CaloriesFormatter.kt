package com.developers.sprintsync.core.util.track_formatter

import kotlin.math.roundToInt

class CaloriesFormatter { // TODO use patterns
    companion object {
        private const val UNIT = "kcal"

        fun formatCalories(
            calories: Float,
            includeUnit: Boolean = false,
        ): String {
            val caloriesInt = calories.roundToInt()
            return if (includeUnit) {
                "$caloriesInt $UNIT"
            } else {
                caloriesInt.toString()
            }
        }
    }
}
