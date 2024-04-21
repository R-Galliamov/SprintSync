package com.developers.sprintsync.tracking.model

sealed class TrackerState {
    object Tracking : TrackerState()

    object Paused : TrackerState()

    object Finished : TrackerState()
}
