package com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter.axis

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

/**
 * A [ValueFormatter] that converts a float value representing days since a reference timestamp
 * into a day of the week label (e.g., "Mon", "Tue",etc.).
 *
 * @param referenceTimestamp The timestamp (in milliseconds) from which to calculate the days.
 */
class ChartWeekDayFormatter(
    private val referenceTimestamp: Long,
) : ValueFormatter() {
    private val dayOfWeekFormat = SimpleDateFormat(DAY_OF_WEEK_PATTERN, Locale.getDefault())

    /**
     * Formats the given float value into a day of the week label.
     *
     * @param value The float value representing days since the reference timestamp.
     * @return The formatted day of the week label.
     */
    override fun getFormattedValue(value: Float): String {
        val timestamp = Instant.ofEpochMilli(referenceTimestamp).plus(value.toLong(), ChronoUnit.DAYS).toEpochMilli()
        return dayOfWeekFormat.format(Date(timestamp))
    }

    companion object {
        private const val DAY_OF_WEEK_PATTERN = "EEE"
    }
}
