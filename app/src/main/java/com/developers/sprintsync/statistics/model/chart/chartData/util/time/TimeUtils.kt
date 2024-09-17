package com.developers.sprintsync.statistics.model.chart.chartData.util.time

import java.util.Calendar

object TimeUtils {
    private const val START_OF_DAY = 0

    fun startOfDayTimestamp(timestamp: Long): Long {
        val calendar =
            Calendar.getInstance().apply { timeInMillis = timestamp }
        calendar.set(Calendar.HOUR_OF_DAY, START_OF_DAY)
        calendar.set(Calendar.MINUTE, START_OF_DAY)
        calendar.set(Calendar.SECOND, START_OF_DAY)
        calendar.set(Calendar.MILLISECOND, START_OF_DAY)
        return calendar.timeInMillis
    }

    fun shiftTimestampByDays(
        timestamp: Long,
        shiftDays: Int,
    ): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        calendar.add(Calendar.DAY_OF_YEAR, shiftDays)
        return calendar.timeInMillis
    }
}
