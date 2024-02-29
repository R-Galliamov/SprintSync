package com.developers.sprintsync.tracking.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.developers.sprintsync.service.tracker.ServiceNotificationHelper
import com.developers.sprintsync.tracking.repository.TrackingRepository
import com.developers.sprintsync.tracking.service.TrackingServiceController.Action.PAUSE_SERVICE
import com.developers.sprintsync.tracking.service.TrackingServiceController.Action.START_SERVICE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {
    @Inject
    lateinit var notificationManager: ServiceNotificationHelper

    @Inject
    lateinit var repository: TrackingRepository

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            START_SERVICE -> {
                Log.i("My stack", "Service started")
                // initForegroundUpdates()
                startForegroundService()
                startCoroutine()
                repository.startTracking()
            }

            PAUSE_SERVICE -> {
                Log.i("My stack", "Service paused")
                repository.stopTracking()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startCoroutine() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.track.collect {
                Log.i("My stack", "Test value is: $it")
            }
        }
    }

    private fun startForegroundService() {
        val id = ServiceNotificationHelper.NOTIFICATION_ID
        val foregroundServiceType = notificationManager.notification.build()
        startForeground(
            id,
            foregroundServiceType,
        )
    }

    private fun initForegroundUpdates() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.track.collect { track ->
                Log.i("My stack", "Service track is: $track")
                notificationManager.updateDuration(track.durationMillis)
                notificationManager.updateDistance(track.distanceMeters)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }
}
