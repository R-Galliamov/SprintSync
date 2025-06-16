package com.developers.sprintsync.presentation.workout_session.notification

import android.widget.RemoteViews
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.log.AppLogger
import javax.inject.Inject

/**
 * Updates notification layout with display data.
 */
class NotificationLayoutUpdater @Inject constructor(val layout: RemoteViews, private val log: AppLogger) {
    /**
     * Updates the notification layout with duration and distance.
     * @param data Display model containing duration and distance.
     */
    fun update(data: TrackingNotificationDisplayModel) {
        try {
            layout.setTextViewText(R.id.tvDurationValue, data.duration)
            layout.setTextViewText(R.id.tvDistanceValue, data.distance)
            log.d("Notification layout updated: duration=${data.duration}, distance=${data.distance}")
        } catch (e: Exception) {
            log.e("Error updating notification layout: ${e.message}", e)
        }
    }
}