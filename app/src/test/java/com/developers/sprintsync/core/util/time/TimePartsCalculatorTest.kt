package com.developers.sprintsync.core.util.time

import com.developers.sprintsync.presentation.components.TimeParts
import com.developers.sprintsync.presentation.components.TimePartsCalculator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class TimePartsCalculatorTest {
    private val calculator = TimePartsCalculator()

    @Test
    fun calculatePartsWithZeroDurationReturnsZeroParts() {
        val durationMillis = 0L
        val expected = TimeParts(0, 0, 0)
        val result = calculator.calculateParts(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun calculatePartsWithOneMinuteReturnsCorrectParts() {
        val durationMillis = TimeUnit.MINUTES.toMillis(1) // 60,000 ms
        val expected = TimeParts(0, 1, 0)
        val result = calculator.calculateParts(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun calculatePartsWithOneHourThirtyMinutesReturnsCorrectParts() {
        val durationMillis = TimeUnit.MINUTES.toMillis(90) // 5,400,000 ms
        val expected = TimeParts(1, 30, 0)
        val result = calculator.calculateParts(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun calculatePartsWithOneMinuteThirtySecondsReturnsCorrectParts() {
        val durationMillis = 90000L // 90,000 ms = 1 min 30 sec
        val expected = TimeParts(0, 1, 30)
        val result = calculator.calculateParts(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun calculatePartsWithSmallDurationReturnsCorrectParts() {
        val durationMillis = 1500L // 1.5 seconds
        val expected = TimeParts(0, 0, 1)
        val result = calculator.calculateParts(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun calculatePartsWithMaxIntDurationReturnsCorrectParts() {
        val durationMillis = 2_147_483_647L // Max Int value in ms (~24.8 days)
        val hours = TimeUnit.MILLISECONDS.toHours(durationMillis).toInt()
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60).toInt()
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60).toInt()
        val expected = TimeParts(hours, minutes, seconds)
        val result = calculator.calculateParts(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun calculatePartsWithNegativeDurationThrowsException() {
        val durationMillis = -90000L
        assertThrows(IllegalArgumentException::class.java) {
            calculator.calculateParts(durationMillis)
        }
    }
}
