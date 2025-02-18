package com.developers.sprintsync.tracking.data.model

data class TimedLocation(
    val location: LocationModel,
    val timestampMillis: Long,
)
