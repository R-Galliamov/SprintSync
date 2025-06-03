package com.developers.sprintsync.presentation.workouts_stats.data

import com.developers.sprintsync.core.util.formatter.DateFormatter
import com.developers.sprintsync.core.util.timestamp.TimestampUtils
import com.developers.sprintsync.presentation.workouts_stats.chart.navigator.RangePosition

data class DateRange(
    val dayMonthRange: String,
    val yearsRange: String,
    val position: RangePosition,
) {
    companion object {
        fun create(
            referenceTimestamp: Long,
            rangePosition: RangePosition,
            firstDayIndex: Int,
            lastDayIndex: Int,
        ): DateRange = Formatter.format(referenceTimestamp, rangePosition, firstDayIndex, lastDayIndex)

        private const val EMPTY_STRING = ""
        val EMPTY = DateRange(EMPTY_STRING, EMPTY_STRING, RangePosition.NOT_INITIALIZED)
    }

    private object Formatter {
        fun format(
            referenceTimestamp: Long,
            rangePosition: RangePosition,
            firstDayIndex: Int,
            lastDayIndex: Int,
        ): DateRange {
            val dayMonthRange =
                getFormattedRange(referenceTimestamp, firstDayIndex, lastDayIndex, DateFormatter.Pattern.DAY_MONTH)
            val yearsRange =
                getFormattedRange(referenceTimestamp, firstDayIndex, lastDayIndex, DateFormatter.Pattern.YEAR)
            return DateRange(dayMonthRange, yearsRange, rangePosition)
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
        ): Long = TimestampUtils.addDaysToTimestamp(referenceTimestamp, dayIndex)

        private const val RANGE_SEPARATOR = "-"
    }
}
