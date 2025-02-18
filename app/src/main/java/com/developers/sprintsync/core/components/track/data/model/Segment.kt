package com.developers.sprintsync.core.components.track.data.model

import com.developers.sprintsync.tracking.data.model.LocationModel

sealed class Segment {
    abstract val id: Long
    abstract val startTime: Long
    abstract val endTime: Long
    abstract val durationMillis: Long

    data class Active(
        override val id: Long,
        val startLocation: LocationModel,
        override val startTime: Long,
        val endLocation: LocationModel,
        override val endTime: Long,
        override val durationMillis: Long,
        val distanceMeters: Int,
        val pace: Float,
        val calories: Int,
    ) : Segment()

    data class Stationary(
        override val id: Long,
        val location: LocationModel,
        override val startTime: Long,
        override val endTime: Long,
        override val durationMillis: Long,
    ) : Segment()
}