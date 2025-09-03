package com.developers.sprintsync.core.components.track.presentation.track_formatter

import com.developers.sprintsync.presentation.components.formatter.DurationFormatStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class MmUnitFormatStrategyTest {
    private val mmUnitStrategy = DurationFormatStrategy.MmUnit

    @Test
    fun formatWithZeroDurationReturnsZeroMinutesWithUnit() {
        val milliseconds = 0L
        val expected = "0 min"
        val result = mmUnitStrategy.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithOneMinuteReturnsCorrectMinutesWithUnit() {
        val milliseconds = TimeUnit.MINUTES.toMillis(1) // 60,000 ms
        val expected = "1 min"
        val result = mmUnitStrategy.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithOneHourReturnsCorrectMinutesWithUnit() {
        val milliseconds = TimeUnit.HOURS.toMillis(1) // 3,600,000 ms
        val expected = "60 min"
        val result = mmUnitStrategy.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithOneMinuteThirtySecondsReturnsCorrectMinutesWithUnit() {
        val milliseconds = 90_000L // 1 minute 30 seconds
        val expected = "1 min" // Truncated to 1 minute
        val result = mmUnitStrategy.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithSmallDurationReturnsZeroMinutesWithUnit() {
        val milliseconds = 30_000L // 30 seconds
        val expected = "0 min" // Less than 1 minute
        val result = mmUnitStrategy.format(milliseconds)
        assertEquals(expected, result)
    }

    @Test
    fun formatWithNegativeDurationThrowsException() {
        val milliseconds = -60_000L
        assertThrows(IllegalArgumentException::class.java) {
            mmUnitStrategy.format(milliseconds)
        }
    }
}
