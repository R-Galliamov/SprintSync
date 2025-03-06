package com.developers.sprintsync.core.util.time

import java.util.Locale
import java.util.concurrent.TimeUnit

data class TimeParts(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
) {
    companion object {
        fun create(milliseconds: Long): TimeParts = TimePartsCalculator().calculateParts(milliseconds)
    }
}

class TimePartsCalculator {
    fun calculateParts(
        milliseconds: Long,
        locale: Locale = Locale.getDefault(),
    ): TimeParts {
        if (milliseconds < 0) throw IllegalArgumentException("Duration cannot be negative")
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds).toInt()
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(milliseconds) % MINUTES_IN_HOUR).toInt()
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(milliseconds) % SECONDS_IN_MINUTE).toInt()
        return TimeParts(hours, minutes, seconds)
    }

    companion object {
        private const val MINUTES_IN_HOUR = 60
        private const val SECONDS_IN_MINUTE = 60
    }
}
