package com.developers.sprintsync.tracking.session.model.session

import com.developers.sprintsync.tracking.session.model.track.LocationModel
import com.developers.sprintsync.tracking.session.model.track.Track

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
