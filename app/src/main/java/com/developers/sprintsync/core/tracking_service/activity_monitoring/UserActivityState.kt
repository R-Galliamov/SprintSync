package com.developers.sprintsync.core.tracking_service.activity_monitoring

sealed class UserActivityState {
    object Running : UserActivityState()

    object HasSlowedDown : UserActivityState()

    object HasStopped : UserActivityState()
}
