package com.developers.sprintsync.tracking.session.model.session

sealed class TrackerState {
    object Initialised : TrackerState()

    object Tracking : TrackerState()

    object Paused : TrackerState()

    object Finished : TrackerState()
}
