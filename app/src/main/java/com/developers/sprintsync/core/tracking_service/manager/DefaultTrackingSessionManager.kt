package com.developers.sprintsync.core.tracking_service.manager

import com.developers.sprintsync.core.tracking_service.data.model.session.TrackingSession
import com.developers.sprintsync.core.tracking_service.orchestrator.TrackSessionOrchestrator
import com.developers.sprintsync.core.tracking_service.orchestrator.TrackSessionOrchestratorState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTrackingSessionManager
    @Inject
    constructor(
        private val trackSessionOrchestrator: TrackSessionOrchestrator,
    ) : TrackingSessionManager {
        override val trackSessionOrchestratorState: StateFlow<TrackSessionOrchestratorState>
            get() = trackSessionOrchestrator.state
        override val data: StateFlow<TrackingSession>
            get() = trackSessionOrchestrator.data

        override fun startUpdatingLocation() {
            trackSessionOrchestrator.startUpdatingLocation()
        }

        override fun stopUpdatingLocation() {
            trackSessionOrchestrator.stopUpdatingLocation()
        }

        override fun startTracking() {
            trackSessionOrchestrator.start()
        }

        override fun pauseTracking() {
            trackSessionOrchestrator.pause()
        }

        override fun finishTracking() {
            trackSessionOrchestrator.finish()
        }

        override fun reset() {
            trackSessionOrchestrator.reset()
        }
    }
