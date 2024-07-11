package com.developers.sprintsync.tracking.session.service.tracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_MIN
import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.developers.sprintsync.R
import com.developers.sprintsync.tracking.analytics.dataManager.mapper.indicator.TimeMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TrackingServiceNotifier
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        val notification = getNotificationBuilder()

        private val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        private val notificationLayout =
            RemoteViews(context.packageName, R.layout.service_notification_layout)

        init {
            createTrackingNotificationChannel()
            initStartingValues()
        }

        private fun createTrackingNotificationChannel() {
            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_MIN)
            notificationManager.createNotificationChannel(channel)
        }

        // TODO create a bigContentView
        // TODO change an icon
        private fun getNotificationBuilder(): NotificationCompat.Builder =
            NotificationCompat
                .Builder(context, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_run_48dp)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)

        fun updateDuration(timeInMillis: Long) {
            val presentableTime = TimeMapper.millisToPresentableTime(timeInMillis)
            notificationLayout.setTextViewText(R.id.tvDurationValue, presentableTime)
            notification.setCustomContentView(notificationLayout)
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        }

        fun updateDistance(distanceInMeters: Int) {
            notificationLayout.setTextViewText(R.id.tvDistanceValue, distanceInMeters.toString())
            notification.setCustomContentView(notificationLayout)
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        }

        private fun initStartingValues() {
            updateDistance(0)
            updateDuration(0)
        }

        companion object {
            const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
            const val NOTIFICATION_CHANNEL_NAME = "Tracking location"
            const val NOTIFICATION_ID = 1
        }
    }
