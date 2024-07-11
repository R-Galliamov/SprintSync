package com.developers.sprintsync.tracking.model.session

import com.developers.sprintsync.tracking.model.track.LocationModel
import com.developers.sprintsync.tracking.model.track.Track

data class TrackingSession(
    val currentLocation: LocationModel?,
    val track: Track,
    val durationMillis: Long,
) {
    companion object {
        val DEFAULT =
            TrackingSession(
                currentLocation = null,
                track = Track.EMPTY_TRACK_DATA,
                durationMillis = 0L,
            )
    }
}
