package com.developers.sprintsync.tracking.component.model

import com.developers.sprintsync.core.components.track.data.model.Track

data class TrackState(
    val track: Track,
    val status: TrackingStatus,
) {
    companion object {
        val INITIAL = TrackState(track = Track.INITIAL, TrackingStatus.INITIALIZED)
    }
}