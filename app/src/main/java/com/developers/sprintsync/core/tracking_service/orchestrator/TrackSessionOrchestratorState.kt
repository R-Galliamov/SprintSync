package com.developers.sprintsync.core.tracking_service.orchestrator

sealed class TrackSessionOrchestratorState {
    object Initialised : TrackSessionOrchestratorState()

    object Tracking : TrackSessionOrchestratorState()

    object Paused : TrackSessionOrchestratorState()

    object Finished : TrackSessionOrchestratorState()
}
