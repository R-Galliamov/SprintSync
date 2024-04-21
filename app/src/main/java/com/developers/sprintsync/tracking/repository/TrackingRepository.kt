package com.developers.sprintsync.tracking.repository

import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.model.TrackerState
import com.developers.sprintsync.tracking.model.TrackingSession
import kotlinx.coroutines.flow.StateFlow

interface TrackingRepository {
    val trackerState: StateFlow<TrackerState>

    val data: StateFlow<TrackingSession>

    fun saveTrack(track: Track)

    fun startTracking()

    fun pauseTracking()

    fun finishTracking()
}
