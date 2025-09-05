package com.developers.sprintsync.domain.track.model

data class Track(
    val id: Int,
    val timestamp: Long,
    val durationMillis: Long,
    val distanceMeters: Float,
    val avgPace: Float?,
    val bestPace: Float?,
    val calories: Float,
    val segments: List<Segment>,
) {
    companion object {
        val INITIAL =
            Track(
                id = 0,
                timestamp = 0L,
                durationMillis = 0L,
                distanceMeters = 0f,
                avgPace = null,
                bestPace = null,
                calories = 0f,
                segments = emptyList(),
            )
    }
}