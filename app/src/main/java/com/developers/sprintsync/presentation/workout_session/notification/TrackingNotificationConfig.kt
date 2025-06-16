package com.developers.sprintsync.presentation.workout_session.notification

/**
 * Configuration for tracking notification, provided via DI.
 */
abstract class TrackingNotificationConfig {
    abstract val channelId: String
    abstract val channelName: String
    abstract val notificationId: Int
    abstract val notificationImportance: Int
}