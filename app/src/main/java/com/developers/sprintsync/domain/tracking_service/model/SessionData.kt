package com.developers.sprintsync.domain.tracking_service.model

data class SessionData(
    val userLocation: LocationModel,
    val durationMillis: Long,
)
