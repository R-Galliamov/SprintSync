package com.developers.sprintsync.global.util.extension

fun Int.roundedDownNearestTen(): Int = this - (this % 10)

fun Int.isMultipleOf(other: Int): Boolean = this % other == 0
