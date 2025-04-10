package com.developers.sprintsync.domain.tracking_service.model

data class TimedLocation(
    val location: LocationModel,
    val timestampMillis: Long,
)
