package com.developers.sprintsync.domain.track.model

data class Segment(
    val id: Long,
    val startTime: Long,
    var endTime: Long,
    var durationMillis: Long,
    val startLocation: LocationModel,
    var endLocation: LocationModel,
    var distanceMeters: Float,
    var pace: Float,
    var calories: Float,
)