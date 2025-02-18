package com.developers.sprintsync.core.components.track.presentation.indicator_formatters

enum class DistanceUiPattern(
    val pattern: String,
) {
    PLAIN("%.2f"),
    WITH_UNIT("%.2f km"),
}