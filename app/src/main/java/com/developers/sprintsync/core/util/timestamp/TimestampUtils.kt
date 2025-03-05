package com.developers.sprintsync.core.util.timestamp

import java.util.Calendar

object TimestampUtils {
    private const val START_OF_DAY = 0

    fun getStartOfDayTimestamp(timestamp: Long): Long {
        val calendar =
            Calendar.getInstance().apply { timeInMillis = timestamp }
        calendar.set(Calendar.HOUR_OF_DAY, START_OF_DAY)
        calendar.set(Calendar.MINUTE, START_OF_DAY)
        calendar.set(Calendar.SECOND, START_OF_DAY)
        calendar.set(Calendar.MILLISECOND, START_OF_DAY)
        return calendar.timeInMillis
    }

    fun addDaysToTimestamp(
        timestamp: Long,
        days: Int,
    ): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.timeInMillis
    }
}
