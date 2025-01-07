package com.developers.sprintsync.core.tracking_service.data.model.session

sealed class TrackStatus {
    data object Incomplete : TrackStatus()

    data object Valid : TrackStatus()

    data object Invalid : TrackStatus()
}
