package com.developers.sprintsync.data.track.service.processing.session

import com.developers.sprintsync.domain.track.model.LocationModel

// Represents a location with an associated timestamp
data class TrackPoint(
    val location: LocationModel,
    val timeMs: Long,
    val accuracyM: Float? = null
)
