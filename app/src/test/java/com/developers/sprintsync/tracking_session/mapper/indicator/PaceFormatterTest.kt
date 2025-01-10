package com.developers.sprintsync.tracking_session.mapper.indicator

import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Locale

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
