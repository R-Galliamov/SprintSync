package com.developers.sprintsync.data.track.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Inject

@ServiceScoped
class TrackingNotificationManager
    @Inject
    constructor(
        private val notificationManager: NotificationManager,
        private val notificationLayout: RemoteViews,
        private val notificationBuilder: NotificationCompat.Builder,
        private val config: TrackingNotificationConfig,
    ) {
        init {
            createNotificationChannel()
        }

        fun updateDuration(timeInMillis: Long) {
            val presentableTime = DurationUiFormatter.format(timeInMillis, DurationUiPattern.HH_MM_SS)
            notificationLayout.setTextViewText(R.id.tvDurationValue, presentableTime)
            updateNotification()
        }

        fun updateDistance(distanceInMeters: Float) {
            notificationLayout.setTextViewText(R.id.tvDistanceValue, distanceInMeters.toString())
            updateNotification()
        }

        private fun createNotificationChannel() {
            val channel = NotificationChannel(config.channelId, config.channelName, IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            Log.i("My stack: Notifier", "channel created")
        }

        private fun updateNotification() {
            notificationBuilder.setCustomContentView(notificationLayout)
            notificationManager.notify(config.notificationId, notificationBuilder.build())
        }
    }
