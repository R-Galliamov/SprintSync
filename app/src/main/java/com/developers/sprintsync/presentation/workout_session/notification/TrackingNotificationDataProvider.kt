package com.developers.sprintsync.presentation.workout_session.notification

import kotlinx.coroutines.flow.Flow

/**
 * Provides tracking notification data as a flow.
 */
abstract class TrackingNotificationDataProvider {
    abstract val dataFlow: Flow<TrackingNotificationData>
}