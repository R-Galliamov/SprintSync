package com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator

import com.developers.sprintsync.tracking.analytics.model.FormattedDurationParts
import java.util.Locale
import java.util.concurrent.TimeUnit

// TODO: create enum class for patterns
class DurationFormatter {
    companion object {
        private const val TIME_FORMAT_HH_MM_SS = "%02d:%02d:%02d"
        private const val TIME_FORMAT_HH_MM = "%02d:%02d"
        private const val TIME_FORMAT_SS = ":%02d"

        private const val HOURS_TO_MINUTES = 60
        private const val MINUTES_TO_SECONDS = 60

        private val defaultLocale by lazy { Locale.getDefault() }

        fun formatToHhMm(durationMillis: Long): String = formatDuration(durationMillis, TIME_FORMAT_HH_MM)

        fun formatToHhMmSs(durationMillis: Long): String = formatDuration(durationMillis, TIME_FORMAT_HH_MM_SS)

        fun formatToHhMmAndSs(
            durationMillis: Long,
            locale: Locale = defaultLocale,
        ): FormattedDurationParts {
            val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
            val minutes =
                TimeUnit.MILLISECONDS.toMinutes(durationMillis) % HOURS_TO_MINUTES
            val seconds =
                TimeUnit.MILLISECONDS.toSeconds(durationMillis) % MINUTES_TO_SECONDS

            val hhMmPart = String.format(locale, TIME_FORMAT_HH_MM, hours, minutes)
            val ssPart = String.format(locale, TIME_FORMAT_SS, seconds)

            return FormattedDurationParts(hhMmPart, ssPart)
        }

        private fun formatDuration(
            durationMillis: Long,
            format: String,
        ): String {
            val locale: Locale = Locale.getDefault()
            val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
            val minutes =
                TimeUnit.MILLISECONDS.toMinutes(durationMillis) % HOURS_TO_MINUTES
            val seconds =
                TimeUnit.MILLISECONDS.toSeconds(durationMillis) % MINUTES_TO_SECONDS
            return String.format(locale, format, hours, minutes, seconds)
        }
    }
}
