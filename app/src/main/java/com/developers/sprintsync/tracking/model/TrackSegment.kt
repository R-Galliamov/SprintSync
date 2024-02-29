package com.developers.sprintsync.tracking.model

data class TrackSegment(
    val startLocation: LocationModel,
    val startTime: Long,
    val endLocation: LocationModel,
    val endTime: Long,
    val durationMillis: Long,
    val distanceMeters: Int,
    val pace: Float,
    val burnedKCalories: Int,
)
