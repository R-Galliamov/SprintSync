package com.developers.sprintsync.tracking_session.presentation.tracking.util.state_handler.ui

sealed class UIState {

    data object Initialized : UIState()

    data object Active : UIState()

    data object Paused : UIState()

    data object Completing : UIState()
}
