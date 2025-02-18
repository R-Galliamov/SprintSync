package com.developers.sprintsync.tracking_session.generator.track

import com.developers.sprintsync.tracking.data.processing.track.TrackStatCalculator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class TrackStatCalculatorTest {
    private val calculator = TrackStatCalculator()

    @Test
    fun calculateDuration_returnsCorrectDuration() {
        val previousDuration = 1000L
        val newDuration = 500L
        val expectedDuration = 1500L

        val result = calculator.calculateDuration(previousDuration, newDuration)

        assertEquals(expectedDuration, result)
    }

    @Test
    fun calculateDistance_returnsCorrectDistance() {
        val previousDistanceMeters = 1000
        val newDistanceMeters = 500
        val expectedDistance = 1500

        val result = calculator.calculateDistance(previousDistanceMeters, newDistanceMeters)

        assertEquals(expectedDistance, result)
    }

    @Test
    fun calculateAvgPace_returnsCorrectAvgPace() {
        val previousAvgPace = 5f
        val newPace = 6f
        val expectedAvgPace = 5.5f

        val result = calculator.calculateAvgPace(previousAvgPace, newPace)

        assertEquals(expectedAvgPace, result, 0.01f)
    }

    @Test
    fun calculateMaxPace_returnsPreviousMaxPaceWhenNewPaceIsSmaller() {
        val previousMaxPace = 6f
        val newPace = 5f
        val expectedMaxPace = 6f

        val result = calculator.calculateBestPace(previousMaxPace, newPace)

        assertEquals(expectedMaxPace, result, 0.01f)
    }

    @Test
    fun calculateMaxPace_returnsNewMaxPaceWhenNewPaceIsBigger() {
        val previousMaxPace = 5f
        val newPace = 6f
        val expectedMaxPace = 6f

        val result = calculator.calculateBestPace(previousMaxPace, newPace)

        assertEquals(expectedMaxPace, result, 0.01f)
    }

    @Test
    fun calculateCalories_returnsCorrectCalories() {
        val previousCalories = 100
        val newCalories = 50
        val expectedCalories = 150

        val result = calculator.calculateCalories(previousCalories, newCalories)

        assertEquals(expectedCalories, result)
    }
}
