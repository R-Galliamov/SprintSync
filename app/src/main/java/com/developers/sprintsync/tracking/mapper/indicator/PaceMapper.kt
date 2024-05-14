package com.developers.sprintsync.tracking.mapper.indicator

import java.util.Locale

class PaceMapper {
    companion object {
        fun formatPaceWithTwoDecimals(pace: Float): String {
            return formatPace(pace, 2)
        }

        fun formatPaceWithOneDecimal(pace: Float): String {
            return formatPace(pace, 1)
        }

        private fun formatPace(
            pace: Float,
            decimalPlaces: Int,
        ): String {
            val locale = Locale.getDefault()
            return String.format(locale, "%.${decimalPlaces}f", pace)
        }
    }
}
