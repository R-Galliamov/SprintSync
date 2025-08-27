package com.developers.sprintsync.data.track.service.processing.calculator.pace.hampel

import kotlin.math.exp

/**
 * Exponential Moving Average (EMA) Smoother.
 *
 * This class implements an exponential moving average filter, which is a type of
 * infinite impulse response (IIR) filter that applies weighting factors which
 * decrease exponentially. The weighting for each older datum decreases exponentially,
 * never reaching zero.
 *
 * The formula used for updating the smoothed value is:
 * `value = value + alpha * (x - value)`
 * where `alpha = 1 - e^(-dtSec / tauSec)`.
 *
 * - `value`: The current smoothed value.
 * - `x`: The new input value.
 * - `dtSec`: The time elapsed since the last update in seconds.
 * - `tauSec`: The time constant (also known as the smoothing factor or characteristic time) in seconds.
 *             A smaller `tauSec` makes the filter respond more quickly to changes in the input,
 *             while a larger `tauSec` results in smoother output but with more lag.
 *
 * @property tauSec The time constant in seconds for the EMA calculation. Defaults to 4 seconds.
 *                  A non-positive or non-finite `tauSec` will result in the output directly following the input.
 */
class EmaSmoother(private val tauSec: Float) {
    var value: Float = 0f
        private set

    /**
     * Resets the value of this object to 0f.
     */
    fun reset() {
        value = 0f
    }

    /**
     * Updates the exponential moving average (EMA) filter with a new value.
     *
     * This function implements a first-order low-pass filter, also known as an EMA filter.
     * The filter smooths out fluctuations in the input signal by giving more weight to recent values.
     *
     * The update formula is:
     * `value = value + alpha * (x - value)`
     * where `alpha = 1 - e^(-dtSec / tauSec)`
     *
     * - `value` is the current filtered value.
     * - `x` is the new input value.
     * - `dtSec` is the time elapsed since the last update in seconds.
     * - `tauSec` is the time constant of the filter in seconds. A smaller `tauSec` results in a faster response
     *   to changes in the input, while a larger `tauSec` provides more smoothing.
     *
     * Edge Cases:
     * - If `dtSec` is not finite, less than or equal to 0, or `x` is not finite, the function returns the current `value` without updating.
     * - If `tauSec` is not finite or less than or equal to 0, the filter behaves like a simple assignment (`value = x`),
     *   effectively disabling the filtering.
     *
     * @param dtSec The time delta since the last update, in seconds. Must be a positive finite number.
     * @param x The new input value to incorporate into the filter. Must be a finite number.
     * @return The updated filtered value.
     */
    fun update(dtSec: Float, x: Float): Float {
        if (!dtSec.isFinite() || dtSec <= 0f || !x.isFinite()) return value
        if (!tauSec.isFinite() || tauSec <= 0f) { value = x; return value }
        // alpha = 1 - e^( -dt/tau )
        val alpha = 1f - exp((-dtSec / tauSec).toDouble()).toFloat()
        value += alpha * (x - value)
        return value
    }
}