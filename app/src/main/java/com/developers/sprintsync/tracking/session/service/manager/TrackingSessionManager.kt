package com.developers.sprintsync.tracking.session.service.manager

import com.developers.sprintsync.tracking.session.model.session.TrackerState
import com.developers.sprintsync.tracking.session.model.session.TrackingSession
import kotlinx.coroutines.flow.StateFlow

interface TrackingSessionManager {
    val trackerState: StateFlow<TrackerState>

    val data: StateFlow<TrackingSession>

    fun startUpdatingLocation()

    fun stopUpdatingLocation()

    fun startTracking()

    fun pauseTracking()

    fun finishTracking()

    fun reset()
}
