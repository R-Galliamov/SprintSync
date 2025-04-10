package com.developers.sprintsync.tracking.data.processing.util.calculator.speed

import com.developers.sprintsync.domain.tracking_service.internal.data_processing.calculator.speed.SpeedCalculatorStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class MetersPerMinuteCalculatorStrategyTest {
    private val calculator = SpeedCalculatorStrategy.MetersPerMinuteCalculatorStrategy

    @Test
    fun calculateWithZeroDurationThrowsIllegalArgumentException() {
        val durationMillis = 0L
        val distanceMeters = 1000f
        assertThrows(IllegalArgumentException::class.java) {
            calculator.calculate(durationMillis, distanceMeters)
        }
    }

    @Test
    fun calculateWithNegativeDurationThrowsIllegalArgumentException() {
        val durationMillis = -300000L
        val distanceMeters = 1000f
        assertThrows(IllegalArgumentException::class.java) {
            calculator.calculate(durationMillis, distanceMeters)
        }
    }

    @Test
    fun calculateWithNegativeDistanceThrowsIllegalArgumentException() {
        val durationMillis = 300000L
        val distanceMeters = -1000f
        assertThrows(IllegalArgumentException::class.java) {
            calculator.calculate(durationMillis, distanceMeters)
        }
    }

    @Test
    fun calculateWithZeroDistanceReturnZeroSpeed() {
        val durationMillis = 300000L
        val distanceMeters = 0f
        val expectedSpeedMpm = 0f
        val result = calculator.calculate(durationMillis, distanceMeters)
        assertEquals(expectedSpeedMpm, result, 0.001f)
    }

    @Test
    fun calculateWithNormalDurationReturnsCorrectSpeed() {
        val durationMillis = 300000L // 5 minutes = 300,000 ms
        val distanceMeters = 1000f // 1 km
        val expectedSpeedMpm = 200f // 1000m / (300000ms / 60000ms/min) = 200 m/min
        val result = calculator.calculate(durationMillis, distanceMeters)
        assertEquals(expectedSpeedMpm, result, 0.001f) // Delta for float precision
    }

    @Test
    fun calculateWithShortDurationReturnsHighSpeed() {
        val durationMillis = 60000L // 1 minute
        val distanceMeters = 500f // 0.5 km
        val expectedSpeedMpm = 500f // 500m / 1min = 500 m/min
        val result = calculator.calculate(durationMillis, distanceMeters)
        assertEquals(expectedSpeedMpm, result, 0.001f)
    }

    @Test
    fun calculateWithLongDurationReturnsLowSpeed() {
        val durationMillis = 600000L // 10 minutes
        val distanceMeters = 1000f // 1 km
        val expectedSpeedMpm = 100f // 1000m / 10min = 100 m/min
        val result = calculator.calculate(durationMillis, distanceMeters)
        assertEquals(expectedSpeedMpm, result, 0.001f)
    }

    @Test
    fun calculateWithExtraSmallDurationReturnsAccurateSpeed() {
        val durationMillis = 2000L // 2 seconds
        val distanceMeters = 100f // 100 meters
        val expectedSpeedMpm = 3000f // 100m / (2000ms / 60000ms/min) = 100 / (2/60) = 3000 m/min
        val result = calculator.calculate(durationMillis, distanceMeters)
        assertEquals(expectedSpeedMpm, result, 0.001f) // Delta for float precision
    }
}
