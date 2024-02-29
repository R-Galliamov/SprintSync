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
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {
    @Inject
    lateinit var notificationManager: ServiceNotificationHelper

    @Inject
    lateinit var repository: TrackingRepository

    private var coroutineScope: CoroutineScope? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            START_SERVICE -> {
                Log.i("My stack", "Service started")
                startForegroundService()
                initCoroutineScope()
                startForegroundUpdates()
                repository.startTracking()
            }

            PAUSE_SERVICE -> {
                Log.i("My stack", "Service paused")
                resetCoroutine()
                repository.stopTracking()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initCoroutineScope() {
        coroutineScope = CoroutineScope(Dispatchers.IO)
    }

    private fun startForegroundUpdates() {
        coroutineScope?.launch(CoroutineName("track_distance")) {
            repository.track.collect { track ->
                notificationManager.updateDistance(track.distanceMeters)
                Log.i("My stack", "Service track is: $track")
            }
        }
        coroutineScope?.launch(CoroutineName("track_time")) {
            repository.timeMillis.collect { time ->
                Log.i("My stack", "Service time is: $time")
                notificationManager.updateDuration(time)
            }
        }
    }

    private fun resetCoroutine() {
        coroutineScope?.cancel()
        coroutineScope = null
    }

    private fun startForegroundService() {
        val id = ServiceNotificationHelper.NOTIFICATION_ID
        val foregroundServiceType = notificationManager.notification.build()
        startForeground(
            id,
            foregroundServiceType,
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }
}
