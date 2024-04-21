package com.developers.sprintsync.tracking.model

sealed class UserActivityState {
    object Running : UserActivityState()

    object HasSlowedDown : UserActivityState()

    object HasStopped : UserActivityState()
}
