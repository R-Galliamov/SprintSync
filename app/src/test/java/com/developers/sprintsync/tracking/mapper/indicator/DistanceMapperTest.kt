package com.developers.sprintsync.tracking.mapper.indicator

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class DistanceMapperTest {
    @Test
    fun metersToPresentableDistance_returnsCorrectlyFormattedDistanceInKilometers() {
        // Arrange
        val distanceInMeters = 1500
        val expectedFormattedDistance = "1.50"
        Locale.setDefault(Locale.US)

        // Act
        val result = DistanceMapper.metersToPresentableDistance(distanceInMeters)

        // Assert
        assertEquals(expectedFormattedDistance, result)
    }

    @Test
    fun metersToPresentableDistance_returnsCorrectlyRoundedUpDistance() {
        // Arrange
        val distanceInMeters = 1495
        val expectedDistanceInKilometers = "1.50"
        Locale.setDefault(Locale.US)

        // Act
        val result = DistanceMapper.metersToPresentableDistance(distanceInMeters)

        // Assert
        assertEquals(expectedDistanceInKilometers, result)
    }

    @Test
    fun metersToPresentableDistance_returnsCorrectlyRoundedDownDistance() {
        // Arrange
        val distanceInMeters = 1494
        val expectedDistanceInKilometers = "1.49"
        Locale.setDefault(Locale.US)

        // Act
        val result = DistanceMapper.metersToPresentableDistance(distanceInMeters)

        // Assert
        assertEquals(expectedDistanceInKilometers, result)
    }

    @Test
    fun metersToPresentableDistance_returnsCorrectlyFormattedDistanceInKilometers_withDifferentLocale() {
        // Arrange
        val distanceInMeters = 1500
        val expectedDistanceInKilometers = "1,50"
        Locale.setDefault(Locale.GERMANY)

        // Act
        val result = DistanceMapper.metersToPresentableDistance(distanceInMeters)

        // Assert
        assertEquals(expectedDistanceInKilometers, result)
    }

    @Test
    fun metersToKilometers_returnsCorrectDistanceInKilometers() {
        // Arrange
        val distanceInMeters = 1500
        val expectedDistanceInKilometers = 1.5f

        // Act
        val result = DistanceMapper.metersToKilometers(distanceInMeters)

        // Assert
        assertEquals(expectedDistanceInKilometers, result)
    }
}
