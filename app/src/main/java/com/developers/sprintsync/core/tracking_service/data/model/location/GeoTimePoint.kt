package com.developers.sprintsync.core.tracking_service.data.model.location

data class GeoTimePoint(
    val location: LocationModel,
    val timeMillis: Long,
)
