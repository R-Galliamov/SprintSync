package com.developers.sprintsync.tracking_session.presentation.tracking.util.tracking_panel

sealed class TrackingPanelState {
    data object Initialized : TrackingPanelState()

    data object Active : TrackingPanelState()

    data object Paused : TrackingPanelState()
}