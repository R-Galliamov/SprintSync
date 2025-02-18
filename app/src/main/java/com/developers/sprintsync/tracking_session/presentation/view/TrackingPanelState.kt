package com.developers.sprintsync.tracking_session.presentation.view

sealed class TrackingPanelState {
    data object Initialized : TrackingPanelState()

    data object Active : TrackingPanelState()

    data object Paused : TrackingPanelState()
}