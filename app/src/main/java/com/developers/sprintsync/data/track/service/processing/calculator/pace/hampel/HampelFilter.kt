package com.developers.sprintsync.data.track.service.processing.calculator.pace.hampel

import com.developers.sprintsync.core.util.extension.median
import kotlin.math.abs

/**
 * A Hampel filter is a robust outlier detection algorithm.
 *
 * It works by considering a sliding window of data points and calculating the median and median absolute deviation (MAD) within that window.
 * If a new data point deviates from the median by more than a certain number of MADs (controlled by the parameter 'k'),
 * it is considered an outlier and is replaced by the median of the window. Otherwise, the original data point is kept.
 *
 * This filter is particularly useful for time-series data where occasional spikes or noise need to be smoothed out
 * without significantly distorting the underlying signal.
 *
 * @property windowSize The number of data points to consider in the sliding window.
 *                      A larger window provides more robust outlier detection but may smooth out legitimate rapid changes.
 * @property k The number of median absolute deviations (MADs) a point must exceed to be considered an outlier.
 *             A common value for k is 3.0. A smaller k makes the filter more sensitive to outliers.
 */
class HampelFilter(
    private val windowSize: Int,
    private val k: Float,
) {
    private val window = ArrayDeque<Float>()

    companion object {
        private const val MIN_WINDOW_SIZE_FOR_CALCULATION = 5
        private const val MAD_TO_SIGMA_FACTOR = 1.4826f
        private const val ZERO_F = 0f
    }

    /**
     * Adds a value to a sliding window and clamps it if it's an outlier.
     *
     * The function maintains a sliding window of recent values.
     * If the window size is at least 5, it calculates the median and Median Absolute Deviation (MAD)
     * of the values in the window.
     * The value `x` is considered an outlier if its absolute difference from the median
     * is greater than `k` times the standard deviation (estimated from MAD).
     * If `x` is an outlier, it's clamped to the median. Otherwise, `x` is used as is.
     *
     * The (potentially clamped) value is then added to the end of the window.
     * If the window size exceeds `windowSize`, the oldest value is removed from the beginning.
     *
     * @param x The float value to add to the window.
     * @return The clamped value that was added to the window.
     *
     * Assumptions:
     * - `window`: A `MutableList` or similar structure that supports `toList()`, `addLast()`, `removeFirst()`, and `size`.
     * - `windowSize`: An integer defining the maximum size of the sliding window.
     * - `k`: A float constant used as a threshold multiplier for outlier detection.
     * - `median()`: An extension function or method available on `List<Float>` to calculate the median.
     * - `abs()`: A function to calculate the absolute value of a float.
     */
    fun addAndClamp(x: Float): Float {
        val arr = window.toList()
        val clamped = if (arr.size >= MIN_WINDOW_SIZE_FOR_CALCULATION) {
            val med = arr.median()
            val mad = arr.map { abs(it - med) }.median()
            val sigma = if (mad == ZERO_F) ZERO_F else MAD_TO_SIGMA_FACTOR * mad
            if (sigma > ZERO_F && abs(x - med) > k * sigma) med else x
        } else x

        window.addLast(clamped)
        if (window.size > windowSize) window.removeFirst()
        return clamped
    }

    /**
     * Clears the window.
     * This function calls the `clear()` method of the `window` object,
     * effectively erasing any previously drawn content from the window.
     */
    fun clear() = window.clear()
}