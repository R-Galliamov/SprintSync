package com.developers.sprintsync.core.components.time.utils

class TimestampBuilder(
    private var timestamp: Long,
) {
    fun startOfDayTimestamp(): TimestampBuilder {
        timestamp = TimeUtils.getStartOfDayTimestamp(timestamp)
        return this
    }

    fun shiftTimestampByDays(shiftDays: Int): TimestampBuilder {
        timestamp = TimeUtils.addDaysToTimestamp(timestamp, shiftDays)
        return this
    }

    fun build(): Long = timestamp
}
