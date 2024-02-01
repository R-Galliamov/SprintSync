package com.developers.sprintsync.service

import androidx.lifecycle.LifecycleService
import com.developers.sprintsync.manager.location.LocationManager
import com.developers.sprintsync.model.LocationModel
import com.developers.sprintsync.util.extension.toDataModel
import com.developers.sprintsync.util.stopWatch.StopWatch
import com.developers.sprintsync.util.`typealias`.Track
import com.developers.sprintsync.util.`typealias`.addPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingService @Inject constructor(private val locationManager: LocationManager, private val stopWatch: StopWatch) :
    LifecycleService() {

    private var _isActive = false
    val isActive: Boolean
        get() = _isActive

    private var wasResumed = false

    val location: Flow<LocationModel> =
        locationManager.listenToLocation().map { it.toDataModel() }

    val track: Flow<Track> = location
        .scan(
            listOf(emptyList())
        ) { accumulator, value ->
            val track = accumulator.toMutableList()
            if (isActive) {
                if (wasResumed && track.last().isNotEmpty()) {
                    wasResumed = false
                    track.add(emptyList())
                }
                track.addPoint(value)
            }
            track
        }

    val timeInMillis = stopWatch.timeMillisState

    fun pause() {
        _isActive = false
        stopWatch.pause()
    }

    fun resume() {
        _isActive = true
        wasResumed = true
        stopWatch.start()
    }

}
