package com.developers.sprintsync.tracking.session.model.session

sealed class TrackStatus {
    object Incomplete : TrackStatus()

    object Valid : TrackStatus()

    object Invalid : TrackStatus()
}
