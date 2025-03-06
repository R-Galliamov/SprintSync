package com.developers.sprintsync.core.components.track.presentation.indicator_formatters

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.Locale
import java.util.concurrent.TimeUnit

class MmFormatStrategyTest {
    private val mmStrategy = DurationFormatStrategy.Mm

    @Test
    fun formatWithZeroDurationReturnsZeroMinutes() {
        val milliseconds = 0L
        val expected = "0"
        val result = mmStrategy.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithOneMinuteReturnsCorrectMinutes() {
        val milliseconds = TimeUnit.MINUTES.toMillis(1) // 60,000 ms
        val expected = "1"
        val result = mmStrategy.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithOneHourReturnsCorrectMinutes() {
        val milliseconds = TimeUnit.HOURS.toMillis(1) // 3,600,000 ms
        val expected = "60"
        val result = mmStrategy.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithOneMinuteThirtySecondsReturnsCorrectMinutes() {
        val milliseconds = 90_000L // 1 minute 30 seconds
        val expected = "1" // Truncated to 1 minute
        val result = mmStrategy.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithSmallDurationReturnsZeroMinutes() {
        val milliseconds = 30_000L // 30 seconds
        val expected = "0" // Less than 1 minute
        val result = mmStrategy.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithNegativeDurationThrowsException() {
        val milliseconds = -60_000L
        assertThrows(IllegalArgumentException::class.java) {
            mmStrategy.format(milliseconds)
        }
    }
}
