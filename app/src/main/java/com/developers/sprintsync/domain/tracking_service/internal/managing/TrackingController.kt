package com.developers.sprintsync.domain.tracking_service.internal.managing

import com.developers.sprintsync.domain.tracking_service.model.TrackingStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingController
    @Inject
    constructor(
        private val trackingDataManager: TrackingDataManager,
        private val sessionManager: SessionManager,
    ) {
        val trackingDataFlow = trackingDataManager.trackingDataFlow
        val sessionDataFlow = sessionManager.sessionDataFlow

        fun start() {
            trackingDataManager.updateTrackingStatus(TrackingStatus.ACTIVE) // TODO Add error state checking?
            sessionManager.start { trackingDataManager.updateTimedLocation(it) }
        }

        fun pause() {
            trackingDataManager.updateTrackingStatus(TrackingStatus.PAUSED)
            sessionManager.pause()
        }

        fun stop() {
            trackingDataManager.updateTrackingStatus(TrackingStatus.COMPLETED)
            sessionManager.stop()
        }
    }
