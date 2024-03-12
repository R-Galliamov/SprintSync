package com.developers.sprintsync.global.util.extension

fun Int.roundedDownNearestTen(): Int {
    return this - (this % 10)
}
