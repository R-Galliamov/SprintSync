package com.developers.sprintsync.core.components.track.data.model

import android.graphics.Bitmap

data class Track(
    val id: Int,
    val timestamp: Long,
    val durationMillis: Long,
    val distanceMeters: Int,
    val avgPace: Float,
    val bestPace: Float,
    val calories: Int,
    val segments: Segments,
) {
    companion object {
        val INITIAL =
            Track(
                id = 0,
                timestamp = 0L,
                durationMillis = 0L,
                distanceMeters = 0,
                avgPace = 0f,
                bestPace = 0f,
                calories = 0,
                segments = emptyList(),
            )
    }
}