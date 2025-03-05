package com.developers.sprintsync.core.util.timestamp

class TimestampBuilder(
    private var timestamp: Long,
) {
    fun startOfDayTimestamp(): TimestampBuilder {
        timestamp = TimestampUtils.getStartOfDayTimestamp(timestamp)
        return this
    }

    fun shiftTimestampByDays(shiftDays: Int): TimestampBuilder {
        timestamp = TimestampUtils.addDaysToTimestamp(timestamp, shiftDays)
        return this
    }

    fun build(): Long = timestamp
}
