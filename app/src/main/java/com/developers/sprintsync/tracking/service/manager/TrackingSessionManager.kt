package com.developers.sprintsync.tracking.service.manager

import com.developers.sprintsync.tracking.model.session.TrackerState
import com.developers.sprintsync.tracking.model.session.TrackingSession
import kotlinx.coroutines.flow.StateFlow

interface TrackingSessionManager {
    val trackerState: StateFlow<TrackerState>

    val data: StateFlow<TrackingSession>

    fun startUpdatingLocation()

    fun stopUpdatingLocation()

    fun startTracking()

    fun pauseTracking()

    fun finishTracking()

    fun isTrackValid(): Boolean
}
