package com.developers.sprintsync.presentation.components

import java.util.concurrent.TimeUnit

/**
 * Represents time in terms of hours, minutes, and seconds.
 *
 * This data class provides a structured way to store and access time components.
 * It's typically created from a duration in milliseconds using the companion object's `create` method.
 *
 * @property hours The number of full hours.
 * @property minutes The number of full minutes (0-59), after accounting for hours.
 * @property seconds The number of full seconds (0-59), after accounting for hours and minutes.
 */
data class TimeParts(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
) {
    companion object {
        fun create(milliseconds: Long): TimeParts = TimePartsCalculator().calculateParts(milliseconds)
    }
}

/**
 * A utility class for calculating time parts (hours, minutes, seconds) from a given duration in milliseconds.
 */
class TimePartsCalculator {
    fun calculateParts(
        milliseconds: Long,
    ): TimeParts {
        if (milliseconds < 0) throw IllegalArgumentException("Duration cannot be negative")
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds).toInt()
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(milliseconds) % MINUTES_IN_HOUR).toInt()
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(milliseconds) % SECONDS_IN_MINUTE).toInt()
        return TimeParts(hours, minutes, seconds)
    }

    companion object {
        private const val MINUTES_IN_HOUR = 60
        private const val SECONDS_IN_MINUTE = 60
    }
}
