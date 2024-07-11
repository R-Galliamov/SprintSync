package com.developers.sprintsync.tracking.session.model.track

data class GeoTimePoint(
    val location: LocationModel,
    val timeMillis: Long,
)
