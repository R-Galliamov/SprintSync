package com.developers.sprintsync.tracking.model

data class Track(
    val id: Int,
    val startTimeDateMillis: Long,
    val durationMillis: Long,
    val distanceMeters: Int,
    val avgPace: Float,
    val maxPace: Float,
    val calories: Int,
    val segments: Segments,
) {
    companion object {
        val EMPTY_TRACK_DATA =
            Track(
                id = -1,
                startTimeDateMillis = 0L,
                durationMillis = 0L,
                distanceMeters = 0,
                avgPace = 0f,
                maxPace = 0f,
                calories = 0,
                segments = emptyList(),
            )
    }
}
