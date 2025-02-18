package com.developers.sprintsync.map.presentation.util.calculator

import kotlin.math.max

object MapCalculations {
    fun calculateTrackPadding(
        mapWidth: Int,
        mapHeight: Int,
    ): Int {
        val horizontalPadding = (mapWidth * TRACK_PADDING_PERCENTAGE).toInt()
        val verticalPadding = (mapHeight * TRACK_PADDING_PERCENTAGE).toInt()
        return max(horizontalPadding, verticalPadding)
    }

    private const val TRACK_PADDING_PERCENTAGE = 0.05f
}
