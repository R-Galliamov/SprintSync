package com.developers.sprintsync.statistics.model.chart.chartData.util.time

class TimestampBuilder(
    private var timestamp: Long,
) {
    fun startOfDayTimestamp(): TimestampBuilder {
        timestamp = TimeUtils.startOfDayTimestamp(timestamp)
        return this
    }

    fun shiftTimestampByDays(shiftDays: Int): TimestampBuilder {
        timestamp = TimeUtils.shiftTimestampByDays(timestamp, shiftDays)
        return this
    }

    fun build(): Long = timestamp
}
