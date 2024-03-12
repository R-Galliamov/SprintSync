package com.developers.sprintsync.tracking.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
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
    lateinit var notifier: TrackingServiceNotifier

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
                Log.i("My stack", "Service is started")
                startForegroundService()
                initCoroutineScope()
                startForegroundUpdates()
                repository.startTracking()
            }

            PAUSE_SERVICE -> {
                Log.i("My stack", "Service is paused")
                stopForegroundCoroutine()
                repository.stopTracking()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initCoroutineScope() {
        coroutineScope = CoroutineScope(Dispatchers.IO)
    }

    private fun startForegroundUpdates() {
        startDistanceUpdates()
        startTimeUpdates()
    }

    private fun stopForegroundCoroutine() {
        coroutineScope?.cancel()
        coroutineScope = null
    }

    private fun startDistanceUpdates() {
        coroutineScope?.launch(CoroutineName("track_distance")) {
            repository.track.collect { track ->
                notifier.updateDistance(track.distanceMeters)
                Log.i("My stack", "Service track is: $track")
            }
        }
    }

    private fun startTimeUpdates() {
        coroutineScope?.launch(CoroutineName("track_time")) {
            repository.timeMillis.collect { time ->
                Log.i("My stack", "Service time is: $time")
                notifier.updateDuration(time)
            }
        }
    }

    private fun startForegroundService() {
        val id = TrackingServiceNotifier.NOTIFICATION_ID
        val foregroundServiceType = notifier.notification.build()
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
