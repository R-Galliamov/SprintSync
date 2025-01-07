package com.developers.sprintsync.core.util.extension

import kotlin.math.abs

fun Int.roundedDownNearestTen(): Int = this - (this % 10)

fun Int.isMultipleOf(other: Int): Boolean = this % other == 0

fun Float.approximatelyEquals(
    other: Float,
    tolerance: Float,
): Boolean = abs(this - other) < tolerance
