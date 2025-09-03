package com.developers.sprintsync.presentation.components.formatter

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A utility object for formatting dates.
 */
object DateFormatter {
    /**
     * Formats a given epoch milliseconds timestamp into a human-readable date string.
     *
     * @param epochMillis The timestamp in milliseconds since the epoch (January 1, 1970, 00:00:00 GMT).
     * @param pattern The desired date and time pattern to format the timestamp into.
     *                This uses the `Pattern` enum which defines common date/time formats.
     * @return A string representation of the formatted date and time based on the provided pattern
     *         and the system's default locale and time zone.
     *
     */
    fun formatDate(
        epochMillis: Long,
        pattern: Pattern,
    ): String {
        val locale = Locale.getDefault()
        val instant = Instant.ofEpochMilli(epochMillis)
        val zoneId = ZoneId.systemDefault()
        val formatter = DateTimeFormatter.ofPattern(pattern.format, locale)
            .withZone(zoneId)
        return formatter.format(instant)
    }

    enum class Pattern(
        val format: String,
    ) {
        YEAR("yyyy"),
        DAY_MONTH("dd MMM"),
        DAY_MONTH_YEAR_FULL("dd MMM yyyy"),
        DAY_MONTH_YEAR_WEEK_DAY("dd.MM.yyyy, EEE"),
    }
}
