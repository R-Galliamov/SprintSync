package com.developers.sprintsync.core.util.extension

import kotlin.math.abs
import kotlin.math.roundToInt

fun Int.roundedDownNearestTen(): Int = this - (this % 10)

fun Float.roundedDownNearestTen(): Int = this.roundToInt().roundedDownNearestTen()

fun Int.isMultipleOf(other: Int): Boolean = this % other == 0

fun Float.approximatelyEquals(
    other: Float,
    tolerance: Float,
): Boolean = abs(this - other) < tolerance

fun List<Float>.median(): Float {
    if (isEmpty()) return Float.NaN
    if (any { it.isNaN() }) return Float.NaN
    val s = sorted()
    val n = s.size
    val m = n / 2
    return if (n % 2 == 1) s[m] else (s[m - 1] + s[m]) / 2f
}
