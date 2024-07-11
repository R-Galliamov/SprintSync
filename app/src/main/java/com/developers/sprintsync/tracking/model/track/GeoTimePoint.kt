package com.developers.sprintsync.tracking.model.track

data class GeoTimePoint(
    val location: LocationModel,
    val timeMillis: Long,
)
