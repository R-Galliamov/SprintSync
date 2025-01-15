package com.developers.sprintsync.core.tracking_service.orchestrator

sealed class TrackingState {
    data object Initialised : TrackingState()

    data object Tracking : TrackingState()

    data object Paused : TrackingState()

    data object Finished : TrackingState()
}
