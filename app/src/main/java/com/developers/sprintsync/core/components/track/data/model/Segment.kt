package com.developers.sprintsync.core.components.track.data.model

import com.developers.sprintsync.core.tracking_service.data.model.location.LocationModel

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