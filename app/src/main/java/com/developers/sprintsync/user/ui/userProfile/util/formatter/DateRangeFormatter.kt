package com.developers.sprintsync.user.ui.userProfile.util.formatter

import com.developers.sprintsync.user.model.FormattedDateRange
import com.developers.sprintsync.user.model.chart.navigator.RangePosition
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Locale

class DateRangeFormatter {
    fun formatRange(
        referenceTimestamp: Long,
        rangePosition: RangePosition,
        firstDayIndex: Int,
        lastDayIndex: Int,
    ): FormattedDateRange {
        val dayMonthRange =
            getFormattedRange(referenceTimestamp, firstDayIndex, lastDayIndex, DAY_MONT_RANGE_UNIT_PATTERN)
        val yearsRange = getFormattedRange(referenceTimestamp, firstDayIndex, lastDayIndex, YEAR_RANGE_UNIT_PATTERN)
        return FormattedDateRange(dayMonthRange, yearsRange, rangePosition)
    }

    private fun getFormattedRange(
        referenceTimestamp: Long,
        firstDayIndex: Int,
        lastDayIndex: Int,
        pattern: String,
    ): String {
        val (formattedFirstDate, formattedLastDate) =
            formatDates(
                referenceTimestamp,
                firstDayIndex,
                lastDayIndex,
                pattern,
            )
        return if (formattedFirstDate == formattedLastDate) {
            formattedFirstDate
        } else {
            "$formattedFirstDate $RANGE_SEPARATOR $formattedLastDate"
        }
    }

    private fun formatDates(
        referenceTimestamp: Long,
        firstDayIndex: Int,
        lastDayIndex: Int,
        pattern: String,
    ): Pair<String, String> {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        val timestampFirst =
            calculateTimestamp(referenceTimestamp, firstDayIndex)
        val timestampLast =
            calculateTimestamp(referenceTimestamp, lastDayIndex)
        return Pair(dateFormat.format(timestampFirst), dateFormat.format(timestampLast))
    }

    private fun calculateTimestamp(
        referenceTimestamp: Long,
        dayIndex: Int,
    ): Long {
        val timestampLast =
            Instant
                .ofEpochMilli(referenceTimestamp)
                .plus(dayIndex.toLong(), ChronoUnit.DAYS)
                .toEpochMilli()
        return timestampLast
    }

    companion object {
        private const val DAY_MONT_RANGE_UNIT_PATTERN = "dd MMM"
        private const val YEAR_RANGE_UNIT_PATTERN = "yyyy"
        private const val RANGE_SEPARATOR = "-"
    }
}
