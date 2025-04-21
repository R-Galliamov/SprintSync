package com.developers.sprintsync.domain.track.model

data class SessionData(
    val userLocation: LocationModel?,
    val durationMillis: Long,
) {
    companion object {
        val INITIAL = SessionData(userLocation = null, durationMillis = 0)
    }
}
