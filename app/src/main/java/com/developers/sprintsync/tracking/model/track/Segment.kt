package com.developers.sprintsync.tracking.model.track

typealias Segments = List<Segment>

sealed class Segment {
    data class ActiveSegment(
        val id: Long,
        val startLocation: LocationModel,
        val startTime: Long,
        val endLocation: LocationModel,
        val endTime: Long,
        val durationMillis: Long,
        val distanceMeters: Int,
        val pace: Float,
        val calories: Int,
    ) : Segment()

    data class InactiveSegment(
        val id: Long,
        val location: LocationModel,
        val startTime: Long,
        val endTime: Long,
        val durationMillis: Long,
    ) : Segment()
}
