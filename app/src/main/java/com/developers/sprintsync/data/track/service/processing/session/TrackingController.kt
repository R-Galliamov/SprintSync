package com.developers.sprintsync.data.track.service.processing.session

import com.developers.sprintsync.domain.track.model.SessionData
import com.developers.sprintsync.domain.track.model.TrackingData
import com.developers.sprintsync.domain.track.model.TrackingStatus
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ServiceScoped
class TrackingController
    @Inject
    constructor(
        private val trackingDataManager: TrackingDataManager,
        private val sessionManager: SessionManager,
    ) {
        val trackingDataFlow: Flow<TrackingData> = trackingDataManager.trackingDataFlow
        val sessionDataFlow: Flow<SessionData> = sessionManager.sessionDataFlow

        fun startLocationUpdates() = sessionManager.launchLocationUpdates()

        fun startTracking() {
            trackingDataManager.updateTrackingStatus(TrackingStatus.ACTIVE)
            sessionManager.start { trackingDataManager.updateTimedLocation(it) }
        }

        fun pauseTracking() {
            trackingDataManager.updateTrackingStatus(TrackingStatus.PAUSED)
            sessionManager.pause()
        }

        fun stopTracking() {
            trackingDataManager.updateTrackingStatus(TrackingStatus.COMPLETED)
            sessionManager.stop()
        }

        fun resetTracking() {
            trackingDataManager.resetState()
        }
    }
