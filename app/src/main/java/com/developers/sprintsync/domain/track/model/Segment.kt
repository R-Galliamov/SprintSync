package com.developers.sprintsync.domain.track.model

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
        val distanceMeters: Float,
        val pace: Float,
        val calories: Float,
    ) : Segment()

    data class Stationary(
        override val id: Long,
        val location: LocationModel,
        override val startTime: Long,
        override val endTime: Long,
        override val durationMillis: Long,
    ) : Segment()
}
