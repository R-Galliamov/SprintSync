package com.developers.sprintsync.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.developers.sprintsync.util.calculator.PaceCalculator
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
    lateinit var trackingFlowManager: TrackingFlowHelper


    //private val paceCalculator = PaceCalculator()
    val isActive: StateFlow<Boolean>
        get() = trackingFlowManager.isActive

    //private var needResumption = false

    private var _distanceInMeters = MutableStateFlow(0)
    val distanceInMeters = _distanceInMeters.asStateFlow()

    fun timeInMillisFlow() = trackingFlowManager.timeInMillisFlow()

    fun locationFlow() = trackingFlowManager.locationFlow().also {
        Log.i("My stack", "Location flow is init")
    }

    fun segmentsFlow() = trackingFlowManager.trackSegmentFlow()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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

    /*

    private fun prepareTrack(track: Track): MutableTrack {
        val preparedTrack = track.toMutableList()
        if (isActive && needResumption && preparedTrack.last().isNotEmpty()) {
            needResumption = false
            preparedTrack.add(emptyList())
        }
        return preparedTrack.toMutableList()
    }

     */

    /*
    private fun updateDistanceInMetersState(track: Track, value: LocationModel) {
        val startPoint = track.lastOrNull()?.lastOrNull()
        if (startPoint != null) {
            val distanceInMeters = locationModelManager.distanceBetweenInMeters(startPoint, value)
                .plus(_distanceInMeters.value)
                .toInt()
            _distanceInMeters.value = distanceInMeters
            notificationManager.updateDistance(distanceInMeters)
        }
    }



    private fun updatePaceMinPerKmState(
        currentTimeMillis: Long,
        currentDistanceMeters: Int
    ) {
        val pace = paceCalculator.getCurrentPaceInMinPerKm(currentTimeMillis, currentDistanceMeters)
        //_paceMinutesPerKm.value = pace
    }

     */

    fun start() {
        trackingFlowManager.start()
    }

    fun pause() {
        trackingFlowManager.pause()
    }

    private fun startForegroundService() {
        val id = ServiceNotificationHelper.NOTIFICATION_ID
        val foregroundServiceType = notificationManager.notification.build()
        startForeground(
            id,
            foregroundServiceType
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

