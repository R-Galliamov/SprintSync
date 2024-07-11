package com.developers.sprintsync.tracking.data.model.session

sealed class UserActivityState {
    object Running : UserActivityState()

    object HasSlowedDown : UserActivityState()

    object HasStopped : UserActivityState()
}
