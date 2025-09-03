package com.developers.sprintsync.presentation.components.formatter

import java.util.Locale

class PaceUiFormatter {
    enum class Pattern(
        val pattern: String,
    ) {
        ONE_DECIMAL("%.1f"),
        TWO_DECIMALS("%.2f"),
    }

    companion object {
        fun format(
            pace: Float,
            pattern: Pattern,
        ): String {
            val locale = Locale.getDefault()
            return String.format(locale, pattern.pattern, pace)
        }
    }
}
