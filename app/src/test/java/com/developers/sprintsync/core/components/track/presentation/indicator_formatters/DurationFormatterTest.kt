package com.developers.sprintsync.core.components.track.presentation.indicator_formatters

import com.developers.sprintsync.core.components.track.presentation.model.FormattedDurationParts
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.Locale

class DurationFormatterTest {
    @Test
    fun formatToMmWithZeroDurationReturnsZeroMinutes() {
        val durationMillis = 0L
        val expected = "0"
        val result = DurationFormatter.formatToMm(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun formatToMmWithSmallDurationReturnsCorrectMinutesWithUnits() {
        val durationMillis = 120000L // 2 minutes
        val expected = "2 min"
        val result = DurationFormatter.formatToMm(durationMillis, showUnits = true)
        assertEquals(expected, result)
    }

    @Test
    fun formatToMmWithLargeDurationReturnsCorrectMinutes() {
        val durationMillis = 3600000L // 1 hour
        val expected = "60"
        val result = DurationFormatter.formatToMm(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun formatToHhMmWithZeroDurationReturnsZeroTime() {
        val durationMillis = 0L
        val expected = "00:00"
        val result = DurationFormatter.formatToHhMm(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun formatToHhMmWithOneHourReturnsCorrectTime() {
        val durationMillis = 3600000L // 1 hour
        val expected = "01:00"
        val result = DurationFormatter.formatToHhMm(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun formatToHhMmSsWithZeroDurationReturnsZeroTime() {
        val durationMillis = 0L
        val expected = "00:00:00"
        val result = DurationFormatter.formatToHhMmSs(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun formatToHhMmSsWithOneMinuteThirtySecondsReturnsCorrectTime() {
        val durationMillis = 90000L // 1 minute 30 seconds
        val expected = "00:01:30"
        val result = DurationFormatter.formatToHhMmSs(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun formatToHhMmAndSsWithOneHourReturnsCorrectParts() {
        val durationMillis = 3600000L // 1 hour
        val expected = FormattedDurationParts("01:00", ":00")
        val result = DurationFormatter.formatToHhMmAndSs(durationMillis)
        assertEquals(expected, result)
    }

    @Test
    fun formatToHhMmAndSsWithCustomLocaleUsesLocale() {
        val durationMillis = 3661000L // 1 hour, 1 minute, 1 second
        val customLocale = Locale("fr", "FR") // French locale
        val expected =
            FormattedDurationParts("01:01", ":01") // Format remains same, locale affects number formatting if needed
        val result = DurationFormatter.formatToHhMmAndSs(durationMillis, customLocale)
        assertEquals(expected, result)
    }

    @Test
    fun formatToHhMmAndSsWithSmallDurationReturnsCorrectParts() {
        val durationMillis = 1500L // 1.5 seconds
        val expected = FormattedDurationParts("00:00", ":01")
        val result = DurationFormatter.formatToHhMmAndSs(durationMillis)
        assertEquals(expected, result)
    }
}
