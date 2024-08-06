package com.developers.sprintsync.user.ui.userProfile.util.chart.styling.valueFormatter

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

class ChartDayFormatter(
    private val referenceTimestamp: Long,
) : ValueFormatter() {
    private val dayOfWeekFormat = SimpleDateFormat(DAY_OF_WEEK_PATTERN, Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        val timestamp = Instant.ofEpochMilli(referenceTimestamp).plus(value.toLong(), ChronoUnit.DAYS).toEpochMilli()
        dayOfWeekFormat.format(Date(timestamp))
        return value.toString()
    }

    companion object {
        private const val DAY_OF_WEEK_PATTERN = "EEE"
    }
}
