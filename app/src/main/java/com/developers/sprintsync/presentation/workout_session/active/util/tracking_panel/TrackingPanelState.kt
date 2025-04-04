package com.developers.sprintsync.presentation.workout_session.active.util.tracking_panel

sealed class TrackingPanelState {
    data object Initialized : TrackingPanelState()

    data object Active : TrackingPanelState()

    data object Paused : TrackingPanelState()
}