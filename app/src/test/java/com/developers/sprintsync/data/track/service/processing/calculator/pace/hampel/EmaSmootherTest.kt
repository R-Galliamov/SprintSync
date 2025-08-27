package com.developers.sprintsync.data.track.service.processing.calculator.pace.hampel

import kotlin.math.abs
import kotlin.math.exp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmaSmootherTest {

    private val delta = 1e-6f

    @Test
    fun `getValue initial value`() {
        val s = EmaSmoother(tauSec = 4f)
        assertEquals(0f, s.value, 0f)
    }

    @Test
    fun `getValue initial value with custom tauSec`() {
        val s = EmaSmoother(tauSec = 2f)
        assertEquals(0f, s.value, 0f)
    }

    @Test
    fun `reset after update`() {
        val s = EmaSmoother(tauSec = 4f)
        s.update(1f, 10f)
        s.reset()
        assertEquals(0f, s.value, 0f)
    }

    @Test
    fun `reset multiple times`() {
        val s = EmaSmoother(tauSec = 4f)
        s.reset()
        s.reset()
        assertEquals(0f, s.value, 0f)
    }

    @Test
    fun `update with valid inputs positive tauSec`() {
        val s = EmaSmoother(tauSec = 4f)
        val dt = 1f
        val x = 10f
        val alpha = 1f - exp((-dt / 4f).toDouble()).toFloat()
        val expected = 0f + alpha * (x - 0f)
        val out = s.update(dt, x)
        assertEquals(expected, out, delta)
        assertEquals(expected, s.value, delta)
    }

    @Test
    fun `update with zero dtSec`() {
        val s = EmaSmoother(tauSec = 4f)
        s.update(1f, 5f)
        val before = s.value
        val out = s.update(0f, 10f)
        assertEquals(before, out, 0f)
        assertEquals(before, s.value, 0f)
    }

    @Test
    fun `update with negative dtSec`() {
        val s = EmaSmoother(tauSec = 4f)
        s.update(1f, 5f)
        val before = s.value
        val out = s.update(-1f, 10f)
        assertEquals(before, out, 0f)
        assertEquals(before, s.value, 0f)
    }

    @Test
    fun `update with non finite dtSec NaN`() {
        val s = EmaSmoother(tauSec = 4f)
        s.update(1f, 5f)
        val before = s.value
        val out = s.update(Float.NaN, 10f)
        assertEquals(before, out, 0f)
        assertEquals(before, s.value, 0f)
    }

    @Test
    fun `update with non finite dtSec Infinity`() {
        val s = EmaSmoother(tauSec = 4f)
        s.update(1f, 5f)
        val before = s.value
        val out = s.update(Float.POSITIVE_INFINITY, 10f)
        assertEquals(before, out, 0f)
        assertEquals(before, s.value, 0f)
    }

    @Test
    fun `update with non finite x NaN`() {
        val s = EmaSmoother(tauSec = 4f)
        s.update(1f, 5f)
        val before = s.value
        val out = s.update(1f, Float.NaN)
        assertEquals(before, out, 0f)
        assertEquals(before, s.value, 0f)
    }

    @Test
    fun `update with non finite x Infinity`() {
        val s = EmaSmoother(tauSec = 4f)
        s.update(1f, 5f)
        val before = s.value
        val out = s.update(1f, Float.POSITIVE_INFINITY)
        assertEquals(before, out, 0f)
        assertEquals(before, s.value, 0f)
    }

    @Test
    fun `update with zero tauSec`() {
        val s = EmaSmoother(tauSec = 0f)
        val out = s.update(1f, 7f)
        assertEquals(7f, out, 0f)
        assertEquals(7f, s.value, 0f)
    }

    @Test
    fun `update with negative tauSec`() {
        val s = EmaSmoother(tauSec = -1f)
        val out = s.update(1f, 7f)
        assertEquals(7f, out, 0f)
        assertEquals(7f, s.value, 0f)
    }

    @Test
    fun `update with non finite tauSec NaN`() {
        val s = EmaSmoother(tauSec = Float.NaN)
        val out = s.update(1f, 7f)
        assertEquals(7f, out, 0f)
        assertEquals(7f, s.value, 0f)
    }

    @Test
    fun `update with non finite tauSec Infinity`() {
        val s = EmaSmoother(tauSec = Float.POSITIVE_INFINITY)
        val out = s.update(1f, 7f)
        assertEquals(7f, out, 0f)
        assertEquals(7f, s.value, 0f)
    }

    @Test
    fun `update sequence converging to x`() {
        val s = EmaSmoother(tauSec = 2f)
        var prevErr = Float.POSITIVE_INFINITY
        repeat(10) {
            s.update(0.2f, 1f)
            val err = abs(1f - s.value)
            assertTrue(err <= prevErr + 1e-7f)
            prevErr = err
        }
    }

    @Test
    fun `update with x equal to current value`() {
        val s = EmaSmoother(tauSec = 1f)
        s.update(1_000_000f, 3.5f) // alpha -> 1
        val before = s.value
        val out = s.update(0.5f, before)
        assertEquals(before, out, 0f)
        assertEquals(before, s.value, 0f)
    }

    @Test
    fun `update with very small dtSec relative to tauSec`() {
        val s = EmaSmoother(tauSec = 10f)
        val dt = 0.001f
        val x = 100f
        val alpha = 1f - exp((-dt / 10f).toDouble()).toFloat()
        val expected = alpha * x
        val out = s.update(dt, x)
        assertEquals(expected, out, 1e-4f)
        assertTrue(out < 0.05f) // minimal change
    }

    @Test
    fun `update with very large dtSec relative to tauSec`() {
        val s = EmaSmoother(tauSec = 0.1f)
        val out = s.update(100f, 42f)
        assertEquals(42f, out, 1e-6f)
        assertEquals(42f, s.value, 1e-6f)
    }

    @Test
    fun `update with dtSec tauSec causing exp underflow`() {
        val s = EmaSmoother(tauSec = 1f)
        val out = s.update(1e9f, -8.75f) // exp(-1e9) -> 0
        assertEquals(-8.75f, out, 0f)
        assertEquals(-8.75f, s.value, 0f)
    }

    @Test
    fun `update with dtSec tauSec causing exp overflow though unlikely with negative exponent`() {
        val s = EmaSmoother(tauSec = 0.123f)
        val dt = 0.456f
        val x = 5f
        val before = s.value
        val out = s.update(dt, x)
        // alpha must be within [0,1], value between before and x
        assertTrue(out.isFinite())
        val min = kotlin.math.min(before, x)
        val max = kotlin.math.max(before, x)
        assertTrue(out in min..max)
    }

    @Test
    fun `update result consistency`() {
        val s = EmaSmoother(tauSec = 2f)
        val out = s.update(0.5f, 9f)
        assertEquals(s.value, out, 0f)
    }

    @Test
    fun `getValue after multiple updates`() {
        val s = EmaSmoother(tauSec = 3f)
        var expected = 0f
        fun step(dt: Float, x: Float) {
            val alpha = 1f - exp((-dt / 3f).toDouble()).toFloat()
            expected += alpha * (x - expected)
            s.update(dt, x)
        }
        step(0.5f, 10f)
        step(0.2f, -4f)
        step(1.0f, 7f)
        assertEquals(expected, s.value, 1e-6f)
    }

    @Test
    fun `update with large float values for x and initial value`() {
        val s = EmaSmoother(tauSec = 1f)
        // set large initial value exactly via alpha≈1
        s.update(1e6f, -1e30f)
        val dt = 0.5f
        val x = 1e30f
        val alpha = 1f - exp((-dt / 1f).toDouble()).toFloat()
        val expected = (-1e30f) + alpha * (x - (-1e30f))
        val out = s.update(dt, x)
        assertTrue(out.isFinite())
        assertEquals(expected, out, 1e24f)
    }

    @Test
    fun `update with very small close to zero float values for x`() {
        val s = EmaSmoother(tauSec = 1f)
        val x = 1e-38f
        val out = s.update(0.5f, x)
        assertTrue(abs(out) <= 1e-38f * 2)
    }

    @Test
    fun `update with dtSec significantly larger than tauSec ensuring alpha caps at 1`() {
        val s = EmaSmoother(tauSec = 0.1f)
        val out = s.update(1000f, 2.25f)
        assertEquals(2.25f, out, 0f)
        assertEquals(2.25f, s.value, 0f)
    }

    @Test
    fun `Constructor with zero tauSec`() {
        val s = EmaSmoother(tauSec = 0f)
        assertEquals(0f, s.value, 0f)
    }

    @Test
    fun `Constructor with negative tauSec`() {
        val s = EmaSmoother(tauSec = -1f)
        assertEquals(0f, s.value, 0f)
    }

    @Test
    fun `Constructor with NaN tauSec`() {
        val s = EmaSmoother(tauSec = Float.NaN)
        assertEquals(0f, s.value, 0f)
    }

    @Test
    fun `Constructor with Infinity tauSec`() {
        val s = EmaSmoother(tauSec = Float.POSITIVE_INFINITY)
        assertEquals(0f, s.value, 0f)
    }
}
