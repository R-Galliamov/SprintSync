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
