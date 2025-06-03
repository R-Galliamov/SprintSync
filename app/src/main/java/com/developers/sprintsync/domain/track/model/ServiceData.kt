package com.developers.sprintsync.domain.track.model

data class TrackingData(
    val track: Track,
    val status: TrackingStatus,
) {
    companion object {
        val INITIAL = TrackingData(track = Track.INITIAL, TrackingStatus.INITIALIZED)
    }
}

enum class TrackingStatus {
    INITIALIZED,
    ACTIVE,
    PAUSED,
    COMPLETED,
}

data class SessionData(
    val userLocation: LocationModel?,
    val durationMillis: Long,
) {
    companion object {
        val INITIAL = SessionData(userLocation = null, durationMillis = 0)
    }
}