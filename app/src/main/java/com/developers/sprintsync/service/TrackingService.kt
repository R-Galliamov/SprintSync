package com.developers.sprintsync.service

import androidx.lifecycle.LifecycleService
import com.developers.sprintsync.manager.location.LocationManager
import com.developers.sprintsync.manager.locationModel.LocationModelManager
import com.developers.sprintsync.model.LocationModel
import com.developers.sprintsync.util.mapper.toDataModel
import com.developers.sprintsync.util.stopWatch.StopWatch
import com.developers.sprintsync.util.`typealias`.MutableTrack
import com.developers.sprintsync.util.`typealias`.Track
import com.developers.sprintsync.util.`typealias`.addPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingService @Inject constructor(
    private val locationManager: LocationManager,
    private val locationModelManager: LocationModelManager,
    private val stopWatch: StopWatch
) :
    LifecycleService() {

    private var _isActive = false
    val isActive: Boolean
        get() = _isActive

    private var needResumption = false

    private var _distanceInMeters = MutableStateFlow(0F)
    val distanceInMeters: StateFlow<Float>
        get() = _distanceInMeters

    private var _paceMinutesPerKm = MutableStateFlow(0F)
    val paceMinutesPerKm: StateFlow<Float>
        get() = _paceMinutesPerKm


    val timeInMillis = stopWatch.timeMillisState

    val location: Flow<LocationModel> =
        locationManager.listenToLocation().map {
            it.toDataModel()
        }

    val track: Flow<Track> = location
        .scan(
            listOf(emptyList())
        ) { accumulator, value ->
            val track = prepareTrack(accumulator)
            if (isActive) {
                updateDistanceInMetersState(track, value)
                updatePaceMinutesPerKmState()
                track.addPoint(value)
            }
            track
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
        }
    }

    private fun updatePaceMinutesPerKmState() {
        val timeInMillis = timeInMillis.value
        val distanceInMeters = distanceInMeters.value
        val pace = if (distanceInMeters != 0F) {
            (timeInMillis / 60_000F) / (distanceInMeters / 1000F)
        } else 0F
        _paceMinutesPerKm.value = pace
    }

    fun start() {
        _isActive = true
        stopWatch.start()
    }

    fun pause() {
        _isActive = false
        needResumption = true
        stopWatch.pause()
    }

}
