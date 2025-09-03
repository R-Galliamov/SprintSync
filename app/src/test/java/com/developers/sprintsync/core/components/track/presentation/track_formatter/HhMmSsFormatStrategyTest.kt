package com.developers.sprintsync.core.components.track.presentation.track_formatter

import com.developers.sprintsync.presentation.components.formatter.DurationFormatStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class HhMmSsFormatStrategyTest {
    private val formatter = DurationFormatStrategy.HhMmSs

    @Test
    fun formatWithZeroDurationReturnsZeroTime() {
        val milliseconds = 0L
        val expected = "00:00:00"
        val result = formatter.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithOneMinuteReturnsCorrectTime() {
        val milliseconds = 60_000L // 1 minute
        val expected = "00:01:00"
        val result = formatter.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithOneHourReturnsCorrectTime() {
        val milliseconds = 3_600_000L // 1 hour
        val expected = "01:00:00"
        val result = formatter.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithOneMinuteThirtySecondsReturnsCorrectTime() {
        val milliseconds = 90_000L // 1 minute 30 seconds
        val expected = "00:01:30"
        val result = formatter.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithSmallDurationReturnsCorrectTime() {
        val milliseconds = 1_500L // 1.5 seconds
        val expected = "00:00:01"
        val result = formatter.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithNegativeDurationThrowsException() {
        val milliseconds = -90_000L
        assertThrows(IllegalArgumentException::class.java) {
            formatter.format(milliseconds)
        }
    }

    @Test
    fun formatWithCustomLocaleUsesDefaultLocale() {
        val milliseconds = 3_660_000L // 1 hour, 1 minute
        val expected = "01:01:00" // Default locale (e.g., US) format
        val result = formatter.format(milliseconds)
        assertEquals(expected, result)
    }
}
