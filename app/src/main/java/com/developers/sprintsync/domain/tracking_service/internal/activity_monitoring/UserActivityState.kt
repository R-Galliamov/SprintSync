package com.developers.sprintsync.domain.tracking_service.internal.activity_monitoring

sealed class UserActivityState {
    object Running : UserActivityState()

    object HasSlowedDown : UserActivityState()

    object HasStopped : UserActivityState()
}
