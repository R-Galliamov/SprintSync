package com.developers.sprintsync.core.tracking_service.data.model.session

import com.developers.sprintsync.core.tracking_service.data.model.location.LocationModel
import com.developers.sprintsync.core.components.track.data.model.Track

data class TrackingSession(
    val currentLocation: LocationModel?,
    val track: Track,
    val durationMillis: Long,
    val trackStatus: TrackStatus,
) {
    companion object {
        val DEFAULT =
            TrackingSession(
                currentLocation = null,
                track = Track.EMPTY_TRACK_DATA,
                durationMillis = 0L,
                trackStatus = TrackStatus.Incomplete,
            )
    }
}
