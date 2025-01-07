package com.developers.sprintsync.core.components.track.presentation.indicator_formatters

import java.util.Locale

class PaceFormatter {
    companion object {
        fun formatPaceWithTwoDecimals(pace: Float): String = formatPace(pace, 2)

        fun formatPaceWithOneDecimal(pace: Float): String = formatPace(pace, 1)

        private fun formatPace(
            pace: Float,
            decimalPlaces: Int,
        ): String {
            val locale = Locale.getDefault()
            return String.format(locale, "%.${decimalPlaces}f", pace)
        }
    }
}
