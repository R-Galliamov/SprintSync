package com.developers.sprintsync.data.track.service.processing.session

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.track.model.TrackingStatus
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Inject

// Controls tracking operations and session management
@ServiceScoped
class TrackingController
@Inject
constructor(
    private val trackingDataManager: TrackingDataManager,
    private val sessionManager: SessionManager,
    private val log: AppLogger,
) {

    // Initiates location updates
    fun startLocationUpdates() = sessionManager.launchLocationUpdates()

    // Stops location updates
    fun stopLocationUpdates() = sessionManager.stopLocationUpdates()

    // Starts tracking session and initialize data processing
    fun startTracking() {
        trackingDataManager.updateTrackingStatus(TrackingStatus.ACTIVE)
        sessionManager.start { trackingDataManager.updateTimedLocation(it) }
        log.i("Start tracking")
    }

    // Pauses tracking session
    fun pauseTracking() {
        trackingDataManager.updateTrackingStatus(TrackingStatus.PAUSED)
        sessionManager.pause()
        log.i("Pause tracking")
    }

    // Stops tracking session
    fun stopTracking() {
        trackingDataManager.updateTrackingStatus(TrackingStatus.COMPLETED)
        sessionManager.stop()
        log.i("Stop tracking")
    }
}
