package com.developers.sprintsync.util.mapper.indicator

import java.util.Locale

object PaceMapper {

    fun paceToPresentablePace(pace: Float): String {
        val locale = Locale.getDefault()
        return String.format(locale, "%.2f", pace)
    }
}
