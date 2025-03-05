package com.developers.sprintsync.core.util.time

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class TimeConverterTest {

    @Test
    fun convertFromMillisToMinutesWithPositiveValueReturnsCorrectFloat() {
        val millis = 2000L
        val expectedMinutes = 2000f / 60000f // ~0.0333f
        val result = TimeConverter.convertFromMillis(millis, TimeConverter.TimeUnit.MINUTES)
        assertEquals(expectedMinutes, result, 0.0001f)
    }

    @Test
    fun convertFromMillisToHoursWithPositiveValueReturnsCorrectFloat() {
        val millis = 3600000L // 1 hour
        val expectedHours = 1f
        val result = TimeConverter.convertFromMillis(millis, TimeConverter.TimeUnit.HOURS)
        assertEquals(expectedHours, result, 0.001f)
    }

    @Test
    fun convertFromMillisToHoursWithOneAndHalfHoursReturnsCorrectFloat() {
        val millis = 5400000L // 1.5 hours = 1.5 * 3,600,000 ms
        val expectedHours = 1.5f
        val result = TimeConverter.convertFromMillis(millis, TimeConverter.TimeUnit.HOURS)
        assertEquals(expectedHours, result, 0.001f)
    }

    @Test
    fun convertToMillisFromHoursWithOneAndHalfHoursReturnsCorrectLong() {
        val hours = 1.5f
        val expectedMillis = (1.5f * 3600000f).toLong() // 5,400,000 ms
        val result = TimeConverter.convertToMillis(hours, TimeConverter.TimeUnit.HOURS)
        assertEquals(expectedMillis, result)
    }

    @Test
    fun convertFromMillisToMinutesWithOneAndHalfHoursReturnsCorrectMinutes() {
        val millis = 5400000L // 1.5 hours
        val expectedMinutes = 90f // 1.5 * 60
        val result = TimeConverter.convertFromMillis(millis, TimeConverter.TimeUnit.MINUTES)
        assertEquals(expectedMinutes, result, 0.001f)
    }


    @Test
    fun convertToMillisFromMinutesWithPositiveValueReturnsCorrectLong() {
        val minutes = 0.0333f
        val expectedMillis = (0.0333f * 60000f).toLong() // ~1998L
        val result = TimeConverter.convertToMillis(minutes, TimeConverter.TimeUnit.MINUTES)
        assertEquals(expectedMillis, result) // Allow small rounding delta
    }

    @Test
    fun convertFromMillisWithNegativeValueThrowsException() {
        val millis = -2000L
        assertThrows(IllegalArgumentException::class.java) {
            TimeConverter.convertFromMillis(millis, TimeConverter.TimeUnit.MINUTES)
        }
    }

    @Test
    fun convertToMillisWithNegativeValueThrowsException() {
        val value = -0.0333f
        assertThrows(IllegalArgumentException::class.java) {
            TimeConverter.convertToMillis(value, TimeConverter.TimeUnit.MINUTES)
        }
    }
}