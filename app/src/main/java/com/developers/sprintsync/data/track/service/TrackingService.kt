package com.developers.sprintsync.data.track.service

import android.app.Notification
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.developers.sprintsync.data.track.service.ServiceCommand.FINISH_SERVICE
import com.developers.sprintsync.data.track.service.ServiceCommand.LAUNCH_LOCATION_UPDATES
import com.developers.sprintsync.data.track.service.ServiceCommand.PAUSE_SERVICE
import com.developers.sprintsync.data.track.service.ServiceCommand.START_SERVICE
import com.developers.sprintsync.data.track.service.notification.TrackingNotificationConfig
import com.developers.sprintsync.data.track.service.notification.TrackingNotificationManager
import com.developers.sprintsync.data.track.service.processing.session.SessionManager
import com.developers.sprintsync.data.track.service.processing.session.TrackingController
import com.developers.sprintsync.data.track.service.processing.session.TrackingDataManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

object ServiceCommand {
    const val LAUNCH_LOCATION_UPDATES = "LAUNCH_LOCATION_UPDATES"
    const val START_SERVICE = "START_SERVICE"
    const val PAUSE_SERVICE = "PAUSE_SERVICE"
    const val FINISH_SERVICE = "FINISH_SERVICE"
}

@ServiceScoped
class TrackingServiceDataHolder
    @Inject
    constructor(
        trackingDataManager: TrackingDataManager,
        sessionManager: SessionManager,
    ) {
        val trackingDataFlow = trackingDataManager.trackingDataFlow
        val sessionDataFlow = sessionManager.sessionDataFlow
    }

@AndroidEntryPoint
class TrackingService : LifecycleService() {
    @Inject
    lateinit var notificationManager: TrackingNotificationManager

    @Inject
    lateinit var notificationConfig: TrackingNotificationConfig

    @Inject
    lateinit var notification: Notification

    @Inject
    lateinit var trackingController: TrackingController

    @Inject
    lateinit var data: TrackingServiceDataHolder

    private val binder = LocalBinder()

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            LAUNCH_LOCATION_UPDATES -> launchLocationUpdates()
            START_SERVICE -> startTracking()
            PAUSE_SERVICE -> pauseTracking()
            FINISH_SERVICE -> stopTracking()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun launchLocationUpdates() {
        try {
            trackingController.startLocationUpdates()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch location updates", e)
        }
    }

    private fun startTracking() {
        Log.i(TAG, "Service is started")
        try {
            startForegroundNotification()

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    trackingController.startTracking()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start tracking in coroutine", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground notification", e)
        }
    }

    private fun pauseTracking() {
        Log.i(TAG, "Service is paused")
        try {
            trackingController.pauseTracking()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pause tracking", e)
        }
    }

    private fun stopTracking() {
        Log.i(TAG, "Service is stopped")
        try {
            trackingController.stopTracking()
            stopSelf()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop tracking", e)
        }
    }

    private fun startForegroundNotification() {
        val id = notificationConfig.notificationId
        startForeground(id, notification)
    }

    inner class LocalBinder : Binder() {
        fun getService(): TrackingService = this@TrackingService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    private companion object {
        const val TAG = "TrackingService"
    }
}
