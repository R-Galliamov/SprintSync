package com.developers.sprintsync.tracking.util.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class PaceCalculatorTest {
    @Test
    fun getPaceInMinPerKm_returnsCorrectPace() {
        val durationMillis = 25 * 60 * 1000L
        val coveredMeters = 5_000
        val expectedPace = 5.0f
        val actual = 0.001f
        val pace = PaceCalculator.getPaceInMinPerKm(durationMillis, coveredMeters)
        assertEquals(expectedPace, pace, actual)
    }
}
