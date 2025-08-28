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

    init {
        log.i("TrackingController HashCode: ${this.hashCode()} - INIT")
    }

    // Initiates location updates
    fun startLocationUpdates() = sessionManager.launchLocationUpdates()

    // Stops location updates
    fun stopLocationUpdates() = sessionManager.stopLocationUpdates()

    // Starts or resumes tracking session and initialize data processing
    fun start() {
        trackingDataManager.updateTrackingStatus(TrackingStatus.Active)
        sessionManager.start { trackingDataManager.updateTimedLocation(it) }
        log.i("Start tracking")
    }

    // Stops tracking session
    fun stops() {
        trackingDataManager.updateTrackingStatus(TrackingStatus.Paused)
        sessionManager.stop()
        log.i("Pause tracking")
    }

    // Finishes tracking session
    fun finish() {
        trackingDataManager.updateTrackingStatus(TrackingStatus.Completed)
        sessionManager.stop()
        log.i("Stop tracking")
    }

    // Resets tracking session
    fun reset() {
        trackingDataManager.resetTracking()
        sessionManager.resetSession()
        log.i("Reset tracking")
    }
}
