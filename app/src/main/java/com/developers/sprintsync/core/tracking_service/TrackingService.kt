package com.developers.sprintsync.core.tracking_service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.developers.sprintsync.core.tracking_service.TrackingServiceController.Action.FINISH_SERVICE
import com.developers.sprintsync.core.tracking_service.TrackingServiceController.Action.PAUSE_SERVICE
import com.developers.sprintsync.core.tracking_service.TrackingServiceController.Action.START_SERVICE
import com.developers.sprintsync.core.tracking_service.manager.TrackingSessionManager
import com.developers.sprintsync.core.tracking_service.notification.TrackingServiceNotifier
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
    lateinit var sessionManager: TrackingSessionManager

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
                sessionManager.startTracking()
            }

            PAUSE_SERVICE -> {
                Log.i("My stack", "Service is paused")
                stopForegroundCoroutine()
                sessionManager.pauseTracking()
            }

            FINISH_SERVICE -> {
                Log.i("My stack", "Service is stopped")
                stopForegroundCoroutine()
                sessionManager.finishTracking()
                stopSelf()
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
            sessionManager.data.collect { data ->
                notifier.updateDistance(data.track.distanceMeters)
            }
        }
    }

    private fun startTimeUpdates() {
        coroutineScope?.launch(CoroutineName("track_time")) {
            sessionManager.data.collect { data ->
                notifier.updateDuration(data.durationMillis)
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
        Log.d("My stack", "Service onDestroy")
    }

    override fun onBind(p0: Intent?): IBinder = Binder()
}
