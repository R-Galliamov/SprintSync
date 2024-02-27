package com.developers.sprintsync.service.tracker

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.developers.sprintsync.manager.service.TrackingServiceManager.Action.PAUSE_SERVICE
import com.developers.sprintsync.manager.service.TrackingServiceManager.Action.START_SERVICE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {
    @Inject
    lateinit var notificationManager: ServiceNotificationHelper

    @Inject
    lateinit var routeTracker: RouteTracker

    @Inject
    lateinit var indicators: Indicators

    val isActive: StateFlow<Boolean>
        get() = routeTracker.isActive

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
        initTimeUpdates()
        initDistanceUpdates()
        initSegmentUpdates()
    }

    private fun initTimeUpdates() {
        indicators.initTimeUpdates { time ->
            notificationManager.updateDuration(time)
        }
    }

    private fun initDistanceUpdates() {
        indicators.initDistanceUpdates { distance ->
            notificationManager.updateDistance(distance)
        }
    }

    private fun initSegmentUpdates() {
        CoroutineScope(Dispatchers.IO).launch {
            segmentFlow().collect { segment ->
                indicators.apply {
                    updateDistance(segment.distanceMeters)
                    updatePace(segment.pace)
                    updateKCalories(segment.burnedKCalories)
                }
            }
        }
    }

    private fun start() {
        routeTracker.start()
    }

    private fun pause() {
        routeTracker.pause()
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

/*
 private var _segments: MutableStateFlow<List<TrackSegment>> = MutableStateFlow(emptyList())
    val segments: StateFlow<List<TrackSegment>>
        get() = _segments.asStateFlow()
 */
