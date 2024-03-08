package com.developers.sprintsync.tracking.mapper.indicator

import java.util.Locale

class PaceMapper {
    companion object {
        fun paceToPresentablePace(pace: Float): String {
            val locale = Locale.getDefault()
            return String.format(locale, "%.2f", pace)
        }
    }
}
