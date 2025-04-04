package com.developers.sprintsync.presentation.map_screen

import kotlin.math.max

object MapCalculations { // TODO no use?
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
