package com.developers.sprintsync.core.util.extension

fun <T> MutableList<T>.setOrAdd(
    index: Int,
    element: T,
) {
    if (index < size) {
        this[index] = element
    } else {
        add(element)
    }
}
