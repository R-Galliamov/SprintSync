package com.developers.sprintsync.data.track.service.processing.calculator.pace.hampel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HampelFilterTest {

    private val eps = 1e-6f

    private fun seed(f: HampelFilter, vararg xs: Float) = xs.forEach { f.addAndClamp(it) }

    @Test
    fun `addAndClamp window empty`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        val r = f.addAndClamp(7f)
        assertEquals(7f, r, eps)

        seed(f, 6f, 7f, 8f, 100f)
        val r2 = f.addAndClamp(1000f)
        assertEquals(7f, r2, eps)
    }

    @Test
    fun `addAndClamp window less than 5 elements`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        assertEquals(1f, f.addAndClamp(1f), eps)
        assertEquals(2f, f.addAndClamp(2f), eps)
        assertEquals(3f, f.addAndClamp(3f), eps)
        assertEquals(4f, f.addAndClamp(4f), eps)
    }

    @Test
    fun `addAndClamp window at least 5 elements  no clamping`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 0f, 0f, 1f, 2f, 2f)
        val r = f.addAndClamp(1.5f)
        assertEquals(1.5f, r, eps)
    }

    @Test
    fun `addAndClamp window at least 5 elements  clamping occurs`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 0f, 0f, 1f, 2f, 2f)
        val r = f.addAndClamp(10f)
        assertEquals(1f, r, eps)
    }

    @Test
    fun `addAndClamp window at least 5 elements  MAD is zero`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 5f, 5f, 5f, 5f, 5f)
        val r = f.addAndClamp(7f)
        assertEquals(7f, r, eps)
    }

    @Test
    fun `addAndClamp window at least 5 elements  MAD is zero  x differs`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 2f, 2f, 2f, 2f, 2f)
        val r = f.addAndClamp(3f)
        assertEquals(3f, r, eps)
    }

    @Test
    fun `addAndClamp window at least 5 elements  x equals median  MAD is zero`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 9f, 9f, 9f, 9f, 9f)
        val r = f.addAndClamp(9f)
        assertEquals(9f, r, eps)
    }

    @Test
    fun `addAndClamp window full  element removed`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 0f, 1f, 2f, 3f, 10f)
        val r1 = f.addAndClamp(1000f)
        assertEquals(2f, r1, eps)

        val r2 = f.addAndClamp(6f)
        assertEquals(6f, r2, eps)
    }

    @Test
    fun `addAndClamp positive float values`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 1f, 1.1f, 1.2f, 1.1f, 1.15f)
        val r = f.addAndClamp(1.14f)
        assertEquals(1.14f, r, eps)
    }

    @Test
    fun `addAndClamp negative float values`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, -1f, -2f, -3f, -2f, -1.5f)
        val r = f.addAndClamp(-1.4f)
        assertEquals(-1.4f, r, eps)
    }

    @Test
    fun `addAndClamp mixed positive and negative float values`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, -2f, -1f, 0f, 1f, 2f)
        val r1 = f.addAndClamp(10f)
        assertEquals(0f, r1, eps)
        val r2 = f.addAndClamp(0.5f)
        assertEquals(0.5f, r2, eps)
    }

    @Test
    fun `addAndClamp with zero values`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 0f, 0f, 0f, 0f, 1f)
        assertEquals(0f, f.addAndClamp(0f), eps)
        assertEquals(10f, f.addAndClamp(10f), eps)
    }

    @Test
    fun `addAndClamp with large float values`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 0f, 1f, 2f, 3f, 4f)
        assertEquals(2f, f.addAndClamp(1e20f), eps)
        assertEquals(2f, f.addAndClamp(-1e20f), eps)
    }

    @Test
    fun `addAndClamp with small float values near zero`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, -1e-6f, -2e-6f, 0f, 1e-6f, 2e-6f)
        val r = f.addAndClamp(5e-7f)
        assertEquals(5e-7f, r, 1e-9f)
    }

    @Test
    fun `addAndClamp with Float NaN`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 0f, 1f, 2f, 3f, 4f)
        val r = f.addAndClamp(Float.NaN)
        assertTrue(r.isNaN())
    }

    @Test
    fun `addAndClamp with Float POSITIVE INFINITY`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 0f, 1f, 2f, 3f, 4f)
        val r = f.addAndClamp(Float.POSITIVE_INFINITY)
        assertEquals(2f, r, eps)
    }

    @Test
    fun `addAndClamp with Float NEGATIVE INFINITY`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 0f, 1f, 2f, 3f, 4f)
        val r = f.addAndClamp(Float.NEGATIVE_INFINITY)
        assertEquals(2f, r, eps)
    }

    @Test
    fun `addAndClamp custom windowSize 1`() {
        val f = HampelFilter(windowSize = 1, k = 3f)
        assertEquals(10f, f.addAndClamp(10f), eps)
        assertEquals(20f, f.addAndClamp(20f), eps)
        assertEquals(2f, f.addAndClamp(2f), eps)
    }

    @Test
    fun `addAndClamp custom windowSize 5`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 0f, 1f, 2f, 3f, 4f)
        val r = f.addAndClamp(100f)
        assertEquals(2f, r, eps)
    }

    @Test
    fun `addAndClamp custom k value  e g   k 1f  k 5f `() {
        val f1 = HampelFilter(windowSize = 5, k = 1f)
        val f5 = HampelFilter(windowSize = 5, k = 5f)
        seed(f1, -2f, -1f, 0f, 1f, 2f)
        seed(f5, -2f, -1f, 0f, 1f, 2f)
        val r1 = f1.addAndClamp(3f)
        val r5 = f5.addAndClamp(3f)
        assertEquals(0f, r1, eps)
        assertEquals(3f, r5, eps)
    }

    @Test
    fun `addAndClamp custom k value zero`() {
        val f = HampelFilter(windowSize = 5, k = 0f)
        seed(f, 0f, 1f, 2f, 3f, 4f)
        val r = f.addAndClamp(2.1f)
        assertEquals(2f, r, eps)
    }

    @Test
    fun `addAndClamp custom k value negative`() {
        val f = HampelFilter(windowSize = 5, k = -1f)
        seed(f, 0f, 1f, 2f, 3f, 4f)
        assertEquals(2f, f.addAndClamp(3f), eps)
        assertEquals(2f, f.addAndClamp(2f), eps)
    }

    @Test
    fun `addAndClamp sequence leading to all same values in window`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 1f, 1f, 1f, 1f, 1f)
        val r = f.addAndClamp(10f)
        assertEquals(10f, r, eps)
    }

    @Test
    fun `clear when window is empty`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        f.clear()
        assertEquals(3f, f.addAndClamp(3f), eps)
    }

    @Test
    fun `clear when window has elements`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 0f, 1f, 2f, 3f, 4f)
        f.clear()
        assertEquals(100f, f.addAndClamp(100f), eps)
    }

    @Test
    fun `clear then addAndClamp`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 1f, 2f, 3f)
        f.clear()
        assertEquals(5f, f.addAndClamp(5f), eps)
    }

    @Test
    fun `addAndClamp alternatingly large and small values`() {
        val f = HampelFilter(windowSize = 5, k = 3f)
        seed(f, 0f, 1f, 2f, 3f, 4f)
        assertEquals(2f, f.addAndClamp(1000f), eps)
        assertEquals(2.5f, f.addAndClamp(2.5f), eps)
        assertEquals(2.5f, f.addAndClamp(-1000f), eps)
        assertEquals(2.7f, f.addAndClamp(2.7f), eps)
    }

    @Test
    fun `addAndClamp very large windowSize`() {
        val f = HampelFilter(windowSize = 10000, k = 3f)
        seed(f, 0f, 1f, 2f, 3f, 4f)
        val r = f.addAndClamp(100f)
        assertEquals(2f, r, eps)
    }
}