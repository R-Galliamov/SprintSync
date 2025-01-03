package com.developers.sprintsync.statistics.presentation.statistics.util.formatter

import com.developers.sprintsync.global.util.formatter.DateFormatter
import com.developers.sprintsync.statistics.domain.chart.utils.time.TimeUtils
import com.developers.sprintsync.statistics.domain.chart.navigator.RangePosition
import com.developers.sprintsync.statistics.domain.ui.FormattedDateRange

class DateRangeFormatter {
    fun formatRange(
        referenceTimestamp: Long,
        rangePosition: RangePosition,
        firstDayIndex: Int,
        lastDayIndex: Int,
    ): FormattedDateRange {
        val dayMonthRange =
            getFormattedRange(referenceTimestamp, firstDayIndex, lastDayIndex, DateFormatter.Pattern.DAY_MONTH)
        val yearsRange = getFormattedRange(referenceTimestamp, firstDayIndex, lastDayIndex, DateFormatter.Pattern.YEAR)
        return FormattedDateRange(dayMonthRange, yearsRange, rangePosition)
    }

    private fun getFormattedRange(
        referenceTimestamp: Long,
        firstDayIndex: Int,
        lastDayIndex: Int,
        pattern: DateFormatter.Pattern,
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
        pattern: DateFormatter.Pattern,
    ): Pair<String, String> {
        val timestampFirst =
            shiftTimestampByDays(referenceTimestamp, firstDayIndex)
        val timestampLast =
            shiftTimestampByDays(referenceTimestamp, lastDayIndex)
        val dateFirst = DateFormatter.formatDate(timestampFirst, pattern)
        val dateLast = DateFormatter.formatDate(timestampLast, pattern)
        return Pair(dateFirst, dateLast)
    }

    private fun shiftTimestampByDays(
        referenceTimestamp: Long,
        dayIndex: Int,
    ): Long = TimeUtils.addDaysToTimestamp(referenceTimestamp, dayIndex)

    companion object {
        private const val RANGE_SEPARATOR = "-"
    }
}
