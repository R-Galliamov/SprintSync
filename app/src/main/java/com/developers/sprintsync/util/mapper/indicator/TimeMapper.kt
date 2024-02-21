package com.developers.sprintsync.util.mapper.indicator

import java.util.Locale

object TimeMapper {
    private const val MILLIS_IN_SECOND = 1000
    private const val SECONDS_IN_MINUTE = 60
    private const val MINUTES_IN_HOUR = 60
    private const val HOURS_IN_DAY = 24

    fun millisToPresentableTime(millis: Long): String {
        val seconds = millis / MILLIS_IN_SECOND % SECONDS_IN_MINUTE
        val minutes = millis / (MILLIS_IN_SECOND * SECONDS_IN_MINUTE) % MINUTES_IN_HOUR
        val hours = millis / (MILLIS_IN_SECOND * SECONDS_IN_MINUTE * MINUTES_IN_HOUR) % HOURS_IN_DAY

        val locale = Locale.getDefault()

        return String.format(locale, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun millisToMinutes(millis: Long): Float {
        return millis / (MILLIS_IN_SECOND * SECONDS_IN_MINUTE).toFloat()
    }
}
