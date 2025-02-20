package com.developers.sprintsync.tracking.service.notification

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingNotificationConfig
    @Inject
    constructor() {
        val channelId: String = "tracking_channel"
        val channelName: String = "Tracking location"
        val notificationId: Int = 1
    }