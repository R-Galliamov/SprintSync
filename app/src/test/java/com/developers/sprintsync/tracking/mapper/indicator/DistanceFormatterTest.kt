package com.developers.sprintsync.tracking.mapper.indicator

import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DistanceFormatter
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Locale

@RunWith(JUnit4::class)
class DistanceFormatterTest {
    @Test
    fun metersToPresentableDistance_returnsCorrectlyFormattedDistanceInKilometers() {
        // Arrange
        val distanceInMeters = 1509
        val expectedFormattedDistance = "1.50"
        Locale.setDefault(Locale.US)

        // Act
        val result = DistanceFormatter.metersToPresentableKilometers(distanceInMeters)

        // Assert
        assertEquals(expectedFormattedDistance, result)
    }

    @Test
    fun metersToPresentableDistance_returnsCorrectlyFormattedDistanceInKilometers_withDifferentLocale() {
        // Arrange
        val distanceInMeters = 1500
        val expectedDistanceInKilometers = "1,50"
        Locale.setDefault(Locale.GERMANY)

        // Act
        val result = DistanceFormatter.metersToPresentableKilometers(distanceInMeters)

        // Assert
        assertEquals(expectedDistanceInKilometers, result)
    }

    @Test
    fun metersToKilometers_returnsCorrectDistanceInKilometers() {
        // Arrange
        val distanceInMeters = 1500
        val expectedDistanceInKilometers = 1.5f

        // Act
        val result = DistanceFormatter.metersToKilometers(distanceInMeters)

        // Assert
        assertEquals(expectedDistanceInKilometers, result)
    }
}