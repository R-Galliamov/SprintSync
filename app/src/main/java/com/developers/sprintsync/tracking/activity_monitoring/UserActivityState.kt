package com.developers.sprintsync.tracking.activity_monitoring

sealed class UserActivityState {
    object Running : UserActivityState()

    object HasSlowedDown : UserActivityState()

    object HasStopped : UserActivityState()
}
