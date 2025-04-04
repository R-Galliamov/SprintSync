package com.developers.sprintsync.tracking.service.implementation

import android.app.Notification
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.developers.sprintsync.tracking.component.model.TrackingStatus
import com.developers.sprintsync.tracking.data.flow.DistanceFlowManager
import com.developers.sprintsync.tracking.data.flow.DurationFlowManager
import com.developers.sprintsync.tracking.data.flow.LocationDurationFlowManager
import com.developers.sprintsync.tracking.service.implementation.ServiceCommand.FINISH_SERVICE
import com.developers.sprintsync.tracking.service.implementation.ServiceCommand.PAUSE_SERVICE
import com.developers.sprintsync.tracking.service.implementation.ServiceCommand.START_SERVICE
import com.developers.sprintsync.tracking.service.manager.TrackingStateManager
import com.developers.sprintsync.tracking.service.notification.TrackingNotificationConfig
import com.developers.sprintsync.tracking.service.notification.TrackingNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : LifecycleService() { // TODO Create interface and move to domain
    @Inject
    lateinit var notificationManager: TrackingNotificationManager

    @Inject
    lateinit var notificationConfig: TrackingNotificationConfig

    @Inject
    lateinit var notification: Notification

    @Inject
    lateinit var trackingStateManager: TrackingStateManager

    @Inject
    lateinit var durationFlow: DurationFlowManager

    @Inject
    lateinit var distanceFlow: DistanceFlowManager

    @Inject
    lateinit var locationDurationFlow: LocationDurationFlowManager

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
        trackingStateManager.updateTrackingStatus(TrackingStatus.ACTIVE)

        lifecycleScope.launch(Dispatchers.IO) {
            locationDurationFlow.start(this) { trackingStateManager.updateLocationDuration(it) }
            durationFlow.start(this) { notificationManager.updateDuration(it) }
            distanceFlow.start(this) { notificationManager.updateDistance(it) }
        }
    }

    private fun pauseTracking() {
        Log.i("My stack", "Service is paused")
        trackingStateManager.updateTrackingStatus(TrackingStatus.PAUSED)
        locationDurationFlow.stop()
        distanceFlow.stop()
        durationFlow.stop()
    }

    private fun stopTracking() {
        Log.i("My stack", "Service is stopped")
        locationDurationFlow.stop()
        trackingStateManager.updateTrackingStatus(TrackingStatus.COMPLETED)
        distanceFlow.stop()
        durationFlow.clean()
        notificationManager
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