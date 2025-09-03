package com.developers.sprintsync.presentation.components.formatter

import java.util.Locale
import kotlin.math.roundToInt

class CaloriesUiFormatter {
    enum class Pattern(
        val pattern: String,
    ) {
        PLAIN("%,d"),
        WITH_UNIT("%,d kcal"),
    }

    companion object {
        fun format(
            calories: Float,
            pattern: Pattern,
        ): String {
            val caloriesInt = calories.roundToInt()
            val locale = Locale.getDefault()
            return String.format(locale, pattern.pattern, caloriesInt)
        }
    }
}
