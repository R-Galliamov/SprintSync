package com.developers.sprintsync.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
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
    lateinit var locationTracker: LocationTracker

    val isActive: StateFlow<Boolean>
        get() = locationTracker.isActive

    private var _distanceInMeters = MutableStateFlow(0)
    val distanceInMeters = _distanceInMeters.asStateFlow()

    fun timeInMillisFlow() = locationTracker.timeInMillisFlow()

    fun locationFlow() =
        locationTracker.locationFlow().also {
            Log.i("My stack", "Location flow is init")
        }

    fun segmentsFlow() = locationTracker.trackSegmentFlow()

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        startForegroundService()
        start()
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
    }

    fun start() {
        locationTracker.start()
    }

    fun pause() {
        locationTracker.pause()
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
