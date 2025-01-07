package com.developers.sprintsync.tracking_session.util.calculator

import com.developers.sprintsync.core.tracking_service.data.processing.util.calculator.PaceCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class PaceCalculatorTest {
    @Test
    fun getPaceInMinPerKm_returnsCorrectPace() {
        val durationMillis = 25 * 60 * 1000L
        val coveredMeters = 5_000
        val expectedPace = 5.0f
        val actual = 0.001f
        val pace = PaceCalculator.getPace(durationMillis, coveredMeters)
        assertEquals(expectedPace, pace, actual)
    }
}
