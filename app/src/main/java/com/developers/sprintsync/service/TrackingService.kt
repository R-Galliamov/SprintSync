package com.developers.sprintsync.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.developers.sprintsync.manager.service.TrackingServiceManager.Action.PAUSE_SERVICE
import com.developers.sprintsync.manager.service.TrackingServiceManager.Action.START_SERVICE
import com.developers.sprintsync.model.tracking.TrackSegment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {
    @Inject
    lateinit var notificationManager: ServiceNotificationHelper

    @Inject
    lateinit var routeTracker: RouteTracker

    val isActive: StateFlow<Boolean>
        get() = routeTracker.isActive

    private var _segments: MutableStateFlow<List<TrackSegment>> = MutableStateFlow(emptyList())
    val segments: StateFlow<List<TrackSegment>>
        get() = _segments.asStateFlow()

    private var _distanceInMeters = MutableStateFlow(0)
    val distanceInMeters: StateFlow<Int>
        get() = _distanceInMeters.asStateFlow()

    fun timeInMillisFlow() = routeTracker.timeInMillisFlow()

    fun locationFlow() =
        routeTracker.locationFlow().also {
            Log.i("My stack", "Location flow is init")
        }

    fun segmentFlow() = routeTracker.trackSegmentFlow()

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            START_SERVICE -> {
                startForegroundService()
                start()
            }

            PAUSE_SERVICE -> {
                pause()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            timeInMillisFlow().collect { time ->
                notificationManager.updateDuration(time)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            distanceInMeters.collect { distance ->
                notificationManager.updateDistance(distance)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            segmentFlow().collect { segment ->
                updateDistance(segment.distanceMeters)
            }
        }
    }

    private fun start() {
        routeTracker.start()
    }

    private fun pause() {
        routeTracker.pause()
    }

    private fun updateDistance(distanceInMeters: Int) {
        _distanceInMeters.value += distanceInMeters
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
        return ServiceBinder()
    }

    inner class ServiceBinder : Binder() {
        fun getService(): TrackingService = this@TrackingService
    }
}
