package com.developers.sprintsync.presentation.workout_session.notification

import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.presentation.components.DistanceFormatter
import javax.inject.Inject

/**
 * Maps notification data to display model.
 */
class TrackingNotificationDisplayMapper @Inject constructor(
    private val distanceFormatter: DistanceFormatter,
) {
    /**
     * Maps notification data to a display model.
     * @param data Notification data with distance and duration.
     * @return [TrackingNotificationDisplayModel] for notification display.
     */
    fun map(data: TrackingNotificationData): TrackingNotificationDisplayModel {
        val duration = DurationUiFormatter.format(data.durationMillis, DurationUiPattern.HH_MM_SS)
        val distance = distanceFormatter.format(data.distanceMeters).value
        return TrackingNotificationDisplayModel(duration = duration, distance = distance)
    }
}