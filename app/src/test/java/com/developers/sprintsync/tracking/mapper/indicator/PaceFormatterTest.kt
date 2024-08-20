package com.developers.sprintsync.tracking.mapper.indicator

import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.PaceFormatter
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Locale

@RunWith(JUnit4::class)
class PaceFormatterTest {
    @Test
    fun paceToPresentablePace_returnsCorrectlyFormattedRoundedUpPace() {
        // Arrange
        val pace = 1.235f
        val expected = "1.24"
        Locale.setDefault(Locale.US)

        // Act
        val result = PaceFormatter.formatPaceWithTwoDecimals(pace)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun paceToPresentablePace_returnsCorrectlyFormattedRoundedDownPace() {
        // Arrange
        val pace = 1.234f
        val expected = "1.23"
        Locale.setDefault(Locale.US)

        // Act
        val result = PaceFormatter.formatPaceWithTwoDecimals(pace)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun paceToPresentablePace_returnsCorrectlyFormattedRoundedUpPaceWithDifferentLocale() {
        // Arrange
        val pace = 1.235f
        val expected = "1,24"
        Locale.setDefault(Locale.GERMANY)

        // Act
        val result = PaceFormatter.formatPaceWithTwoDecimals(pace)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun paceToPresentablePace_returnsCorrectlyFormattedRoundedDownPaceWithDifferentLocale() {
        // Arrange
        val pace = 1.234f
        val expected = "1,23"
        Locale.setDefault(Locale.GERMANY)

        // Act
        val result = PaceFormatter.formatPaceWithTwoDecimals(pace)

        // Assert
        assertEquals(expected, result)
    }
}
