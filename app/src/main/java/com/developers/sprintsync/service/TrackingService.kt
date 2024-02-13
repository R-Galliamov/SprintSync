package com.developers.sprintsync.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.manager.location.LocationManager
import com.developers.sprintsync.manager.locationModel.LocationModelManager
import com.developers.sprintsync.model.LocationModel
import com.developers.sprintsync.util.mapper.toDataModel
import com.developers.sprintsync.util.stopwatch.Stopwatch
import com.developers.sprintsync.util.`typealias`.MutableTrack
import com.developers.sprintsync.util.`typealias`.Track
import com.developers.sprintsync.util.`typealias`.addPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var locationModelManager: LocationModelManager

    @Inject
    lateinit var stopwatch: Stopwatch

    @Inject
    lateinit var notificationManager: ServiceNotificationHelper

    private var _isActive = false
    val isActive: Boolean
        get() = _isActive

    private var needResumption = false

    private var _distanceInMeters = MutableStateFlow(0F)
    val distanceInMeters = _distanceInMeters.asStateFlow()

    private var _paceMinutesPerKm = MutableStateFlow(0F)
    val paceMinutesPerKm = _paceMinutesPerKm.asStateFlow()

    fun getTimeInMillisFlow(): Flow<Long> = stopWatch.timeMillisState

    fun getLocationFlow(): Flow<LocationModel> =
        locationManager.listenToLocation().map {
            it.toDataModel()
        }

    fun getTrackFlow(): Flow<Track> = getLocationFlow()
        .scan(
            listOf(emptyList())
        ) { accumulator, value ->
            val track = prepareTrack(accumulator)
            if (isActive) {
                updateDistanceInMetersState(track, value)
                track.addPoint(value)
            }
            Log.i("My stack", "TrackingService thread: " + Thread.currentThread().name)
            track
        }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            getTrackFlow().collect {
                updatePaceMinutesPerKmState()
            }
            getTimeInMillisFlow().collect { value ->
                notificationManager.updateDuration(value)
            }
            distanceInMeters.collect { value ->
                notificationManager.updateDistance(value)
            }
        }
    }


    private fun prepareTrack(track: Track): MutableTrack {
        val preparedTrack = track.toMutableList()
        if (isActive && needResumption && preparedTrack.last().isNotEmpty()) {
            needResumption = false
            preparedTrack.add(emptyList())
        }
        return preparedTrack.toMutableList()
    }

    private fun updateDistanceInMetersState(track: Track, value: LocationModel) {
        val startPoint = track.lastOrNull()?.lastOrNull()
        if (startPoint != null) {
            val distanceInMeters = locationModelManager.distanceBetween(startPoint, value)
                .plus(_distanceInMeters.value)
            _distanceInMeters.value = distanceInMeters
            notificationManager.updateDistance(distanceInMeters)
        }
    }

    //TODO calc current pace
    private fun updatePaceMinutesPerKmState() {
        val timeInMillis = getTimeInMillisFlow().asLiveData().value ?: 0L
        val distanceInMeters = distanceInMeters.value
        val pace = if (distanceInMeters != 0F) {
            (timeInMillis / 60_000F) / (distanceInMeters / 1000F)
        } else 0F
        _paceMinutesPerKm.value = pace
    }

    fun start() {
        _isActive = true
        stopwatch.start()
    }

    fun pause() {
        _isActive = false
        needResumption = true
        stopwatch.pause()
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
