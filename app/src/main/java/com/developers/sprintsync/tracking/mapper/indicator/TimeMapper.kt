package com.developers.sprintsync.tracking.mapper.indicator

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeMapper {
    companion object {
        private const val MILLIS_IN_SECOND = 1000
        private const val SECONDS_IN_MINUTE = 60
        private const val MINUTES_IN_HOUR = 60
        private const val HOURS_IN_DAY = 24

        private const val DATE_FORMAT = "dd.MM.yyyy"
        private const val PRESENTABLE_TIME_FORMAT = "%02d:%02d:%02d"

        private val locale by lazy { Locale.getDefault() }

        fun timestampToPresentableDate(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat(DATE_FORMAT, locale)
            return format.format(date)
        }

        fun millisToPresentableTime(millis: Long): String {
            val seconds = millis / MILLIS_IN_SECOND % SECONDS_IN_MINUTE
            val minutes = millis / (MILLIS_IN_SECOND * SECONDS_IN_MINUTE) % MINUTES_IN_HOUR
            val hours =
                millis / (MILLIS_IN_SECOND * SECONDS_IN_MINUTE * MINUTES_IN_HOUR) % HOURS_IN_DAY

            return String.format(locale, PRESENTABLE_TIME_FORMAT, hours, minutes, seconds)
        }

        fun millisToSeconds(millis: Long): Float = millis / MILLIS_IN_SECOND.toFloat()

        fun millisToMinutes(millis: Long): Float = millis / (MILLIS_IN_SECOND * SECONDS_IN_MINUTE).toFloat()
    }
}
