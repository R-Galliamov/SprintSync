package com.developers.sprintsync.tracking.analytics.dataManager.formatter

import java.util.Locale
import java.util.concurrent.TimeUnit

class DurationFormatter {
    companion object {
        private const val TIME_FORMAT_HH_MM_SS = "%02d:%02d:%02d"
        private const val TIME_FORMAT_HH_MM = "%02d:%02d"

        private const val HOURS_TO_MINUTES = 60
        private const val MINUTES_TO_SECONDS = 60

        private val defaultLocale by lazy { Locale.getDefault() }

        fun formatToHhMmSs(
            durationMillis: Long,
            locale: Locale = defaultLocale,
        ): String = formatDuration(durationMillis, TIME_FORMAT_HH_MM_SS, locale)

        fun formatToHhMm(
            durationMillis: Long,
            locale: Locale = defaultLocale,
        ): String = formatDuration(durationMillis, TIME_FORMAT_HH_MM, locale)

        private fun formatDuration(
            durationMillis: Long,
            format: String,
            locale: Locale = Locale.getDefault(),
        ): String {
            val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
            val minutes =
                TimeUnit.MILLISECONDS.toMinutes(durationMillis) % HOURS_TO_MINUTES
            val seconds =
                TimeUnit.MILLISECONDS.toSeconds(durationMillis) % MINUTES_TO_SECONDS
            return String.format(locale, format, hours, minutes, seconds)
        }
    }
}
