package com.developers.sprintsync.core.tracking_service.manager

import com.developers.sprintsync.core.tracking_service.orchestrator.TrackSessionOrchestratorState
import com.developers.sprintsync.core.tracking_service.data.model.session.TrackingSession
import kotlinx.coroutines.flow.StateFlow

interface TrackingSessionManager {
    val trackSessionOrchestratorState: StateFlow<TrackSessionOrchestratorState>

    val data: StateFlow<TrackingSession>

    fun startUpdatingLocation()

    fun stopUpdatingLocation()

    fun startTracking()

    fun pauseTracking()

    fun finishTracking()

    fun reset()
}
