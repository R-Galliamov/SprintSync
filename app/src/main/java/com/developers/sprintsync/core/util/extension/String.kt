package com.developers.sprintsync.core.util.extension

fun String.toFloatOrNullLocale(): Float? {
    if (isBlank()) return null
    val normalized = replace(',', '.')
    return normalized.toFloatOrNull()
}
