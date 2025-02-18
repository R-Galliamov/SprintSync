package com.developers.sprintsync.tracking_session.mapper.indicator

import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Locale

class DistanceUiFormatterTest {
    @Test
    fun metersToPresentableDistance_returnsCorrectlyFormattedDistanceInKilometers() {
        // Arrange
        val distanceInMeters = 1509
        val expectedFormattedDistance = "1.50"
        Locale.setDefault(Locale.US)

        // Act
        val result = DistanceUiFormatter.format(distanceInMeters)

        // Assert
        assertEquals(expectedFormattedDistance, result)
    }
}
