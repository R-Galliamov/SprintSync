package com.developers.sprintsync.core.util.extension

import org.junit.Assert.*
import org.junit.Test

class MathKtTest {

    private val EPS = 1e-6f

    @Test
    fun `Median of an empty list`() {
        val result = emptyList<Float>().median()
        assertTrue(result.isNaN())
    }

    @Test
    fun `Median of a list with NaN values`() {
        val result = listOf(1f, Float.NaN, 3f).median()
        assertTrue(result.isNaN())
    }

    @Test
    fun `Median of a list with an odd number of elements`() {
        val result = listOf(1f, 2f, 3f).median()
        assertEquals(2f, result, 0f)
    }

    @Test
    fun `Median of a list with an even number of elements`() {
        val result = listOf(1f, 2f, 3f, 4f).median()
        assertEquals(2.5f, result, EPS)
    }

    @Test
    fun `Median of a list with a single element`() {
        val result = listOf(42f).median()
        assertEquals(42f, result, 0f)
    }

    @Test
    fun `Median of a list with duplicate values  odd count `() {
        val result = listOf(1f, 2f, 2f, 3f, 4f).median()
        assertEquals(2f, result, 0f)
    }

    @Test
    fun `Median of a list with duplicate values  even count `() {
        val result = listOf(1f, 1f, 2f, 2f).median()
        assertEquals(1.5f, result, EPS)
    }

    @Test
    fun `Median of a list with all identical values  odd count `() {
        val result = listOf(5f, 5f, 5f).median()
        assertEquals(5f, result, 0f)
    }

    @Test
    fun `Median of a list with all identical values  even count `() {
        val result = listOf(5f, 5f, 5f, 5f).median()
        assertEquals(5f, result, 0f)
    }

    @Test
    fun `Median of a list with positive and negative numbers`() {
        val result = listOf(-10f, -2f, 0f, 1f, 8f).median()
        assertEquals(0f, result, 0f)
    }

    @Test
    fun `Median of a list with only negative numbers`() {
        val result = listOf(-5f, -4f, -3f, -2f, -1f).median()
        assertEquals(-3f, result, 0f)
    }

    @Test
    fun `Median of a list with zero values`() {
        val result = listOf(0f, 0f, 0f, 1f, -1f).median()
        assertEquals(0f, result, 0f)
    }

    @Test
    fun `Median of a list with very large float values`() {
        val result = listOf(-3.3e38f, 1e30f, 3.3e38f).median()
        assertEquals(1e30f, result, 1e24f)
    }

    @Test
    fun `Median of a list with very small  close to zero  float values`() {
        val result = listOf(-1e-6f, -1e-7f, 1e-7f, 1e-6f).median()
        assertEquals(0f, result, 1e-7f)
    }

    @Test
    fun `Median of an unsorted list  odd count `() {
        val result = listOf(3f, 1f, 2f).median()
        assertEquals(2f, result, 0f)
    }

    @Test
    fun `Median of an unsorted list  even count `() {
        val result = listOf(4f, 1f, 3f, 2f).median()
        assertEquals(2.5f, result, EPS)
    }

    @Test
    fun `Median calculation precision check`() {
        val data = listOf(1e-8f, 1e-7f, 1e-6f, 1e-5f)
        val result = data.median() // middle two: 1e-7 and 1e-6 -> 5.5e-7
        assertEquals(5.5e-7f, result, 1e-12f)
    }
}
