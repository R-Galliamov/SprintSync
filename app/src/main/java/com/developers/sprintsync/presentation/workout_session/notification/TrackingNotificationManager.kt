package com.developers.sprintsync.presentation.workout_session.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import androidx.core.app.NotificationCompat
import com.developers.sprintsync.core.util.log.AppLogger
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ServiceScoped
class TrackingNotificationManager
@Inject
constructor(
    private val notificationData: TrackingNotificationDataProvider,
    private val notificationManager: NotificationManager,
    private val notificationLayoutUpdater: NotificationLayoutUpdater,
    private val notificationBuilder: NotificationCompat.Builder,
    private val displayMapper: TrackingNotificationDisplayMapper,
    private val config: TrackingNotificationConfig,
    private val log: AppLogger,
) {

    private var lastNotificationData: TrackingNotificationDisplayModel? = null

    init {
        createNotificationChannel()
    }

    suspend fun launchUpdates() {
        notificationData.dataFlow.map { displayMapper.map(it) }.collect { data ->
            log.d("Notification data: $data")
            if (data == lastNotificationData) return@collect
            lastNotificationData = data
            updateNotification(data)
        }
    }

    private fun createNotificationChannel() {
        try {
            val channel = NotificationChannel(config.channelId, config.channelName, config.notificationImportance)
            log.d("Notification importance: ${config.notificationImportance}")
            notificationManager.createNotificationChannel(channel)
            log.i("Notification channel created: id=${config.channelId}, name=${config.channelName}")
        } catch (e: Exception) {
            log.e("Failed to create notification channel: ${e.message}", e)
        }

    }

    private fun updateNotification(data: TrackingNotificationDisplayModel) {
        try {
            notificationLayoutUpdater.update(data)
            notificationBuilder.setCustomContentView(notificationLayoutUpdater.layout)
            notificationManager.notify(config.notificationId, notificationBuilder.build())
        } catch (e: Exception) {
            log.e("Failed to update notification: ${e.message}", e)
        }
    }
}
