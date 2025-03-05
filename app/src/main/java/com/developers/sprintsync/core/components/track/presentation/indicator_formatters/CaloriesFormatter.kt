package com.developers.sprintsync.core.components.track.presentation.indicator_formatters

import kotlin.math.roundToInt

class CaloriesFormatter {
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
