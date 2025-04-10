package com.developers.sprintsync.tracking.service.implementation

import android.app.Notification
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.developers.sprintsync.domain.tracking_service.internal.managing.TrackingController
import com.developers.sprintsync.tracking.service.implementation.ServiceCommand.FINISH_SERVICE
import com.developers.sprintsync.tracking.service.implementation.ServiceCommand.PAUSE_SERVICE
import com.developers.sprintsync.tracking.service.implementation.ServiceCommand.START_SERVICE
import com.developers.sprintsync.tracking.service.notification.TrackingNotificationConfig
import com.developers.sprintsync.tracking.service.notification.TrackingNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : LifecycleService() { // TODO Should it be UI level?
    @Inject
    lateinit var notificationManager: TrackingNotificationManager

    @Inject
    lateinit var notificationConfig: TrackingNotificationConfig

    @Inject
    lateinit var notification: Notification

    @Inject
    lateinit var serviceController: TrackingController

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            START_SERVICE -> startTracking()
            PAUSE_SERVICE -> pauseTracking()
            FINISH_SERVICE -> stopTracking()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTracking() {
        Log.i("My stack", "Service is started")
        startForegroundNotification()

        lifecycleScope.launch(Dispatchers.IO) {
            serviceController.start()
        }
    }

    private fun pauseTracking() {
        Log.i("My stack", "Service is paused")
        serviceController.pause()
    }

    private fun stopTracking() {
        Log.i("My stack", "Service is stopped")
        serviceController.stop()
        stopSelf()
    }

    private fun startForegroundNotification() {
        val id = notificationConfig.notificationId
        startForeground(id, notification)
    }
}

object ServiceCommand {
    const val START_SERVICE = "START_SERVICE"
    const val PAUSE_SERVICE = "PAUSE_SERVICE"
    const val FINISH_SERVICE = "FINISH_SERVICE"
}
