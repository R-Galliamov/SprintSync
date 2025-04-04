package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.ui

sealed class UIState {

    data object Initialized : UIState()

    data object Active : UIState()

    data object Paused : UIState()

    data object Completing : UIState()
}
