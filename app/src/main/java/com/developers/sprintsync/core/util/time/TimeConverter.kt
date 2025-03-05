package com.developers.sprintsync.core.util.time

/**
 * Utility class for converting time values between milliseconds and other units.
 */
object TimeConverter {

    enum class TimeUnit(val factor: Float) {
        SECONDS(1000f),  // 1,000 ms per second
        MINUTES(60000f), // 60,000 ms per minute
        HOURS(3600000f)  // 3,600,000 ms per hour
    }

    /**
     * Converts a time value from milliseconds to the specified unit.
     * @param millis The time in milliseconds.
     * @param toUnit The target time unit.
     * @return The converted value as a Float.
     * @throws IllegalArgumentException if millis is negative.
     */
    fun convertFromMillis(millis: Long, toUnit: TimeUnit): Float {
        if (millis < 0) throw IllegalArgumentException("Time in milliseconds cannot be negative")
        return millis / toUnit.factor
    }

    /**
     * Converts a time value from a specified unit to milliseconds.
     * @param value The time value in the source unit.
     * @param fromUnit The source time unit.
     * @return The converted value in milliseconds as a Long.
     * @throws IllegalArgumentException if value is negative.
     */
    fun convertToMillis(value: Float, fromUnit: TimeUnit): Long {
        if (value < 0) throw IllegalArgumentException("Time value cannot be negative")
        return (value * fromUnit.factor).toLong()
    }
}
