package com.developers.sprintsync.domain.track.model

data class TrackingData(
    val track: Track,
    val status: TrackingStatus,
) {
    companion object {
        val INITIAL = TrackingData(track = Track.INITIAL, TrackingStatus.Initialized)
    }
}

sealed class TrackingStatus {
    data object Initialized : TrackingStatus()
    data object Active : TrackingStatus()
    data object Paused : TrackingStatus()
    data class Completed(val onFinish: () -> Unit) : TrackingStatus()
}

data class SessionData(
    val userLocation: LocationModel?,
    val durationMillis: Long,
) {
    companion object {
        val INITIAL = SessionData(userLocation = null, durationMillis = 0)
    }
}