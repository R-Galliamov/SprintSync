package com.developers.sprintsync.service

import android.util.Log
import com.developers.sprintsync.manager.location.LocationManager
import com.developers.sprintsync.manager.locationModel.LocationModelManager
import com.developers.sprintsync.model.tracking.LocationModel
import com.developers.sprintsync.model.tracking.TrackSegment
import com.developers.sprintsync.model.tracking.toDataModel
import com.developers.sprintsync.util.stopwatch.Stopwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


class TrackingFlowHelper @Inject constructor(
    private val locationManager: LocationManager,
    private val locationModelManager: LocationModelManager,
    private val stopwatch: Stopwatch
) {

    private val emptySegment = TrackSegment()
    private var lastLocation: LocationModel? = null
    private var lastTimeStamp: Long = 0L
    private var currentSegment = emptySegment

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean>
        get() = _isActive

    init {
        initActiveStateListener()
    }

    fun locationFlow(): Flow<LocationModel> =
        locationManager.listenToLocation().map { location ->
            location.toDataModel()
        }

    fun timeInMillisFlow(): Flow<Long> = stopwatch.timeMillisState

    fun trackSegmentFlow() =
        locationFlow().combine(timeInMillisFlow()) { location, timeMillis ->
            location to timeMillis
        }.map { pair ->
            val location = pair.first
            val time = pair.second
            val isActive = _isActive.value
            if (isActive && checkLocationChanged(location)) {
                updateLastLocationTime(location, time)
                val segment = getUpdatedSegment(location, time)
                setCurrentSegment(segment)
                getSegmentOrNullIfHasNoStartEndData(segment)
            } else null
        }.filterNotNull()

    fun start() {
        _isActive.value = true
    }

    fun pause() {
        _isActive.value = false
    }

    private fun initActiveStateListener() {
        CoroutineScope(Dispatchers.IO).launch {
            _isActive.collect { isActive ->
                updateStopwatchState(isActive)
                clearCurrentSegment()
            }
        }
    }

    private fun updateStopwatchState(isActive: Boolean) {
        when (isActive) {
            true -> stopwatch.start()
            false -> stopwatch.pause()
        }
    }

    private fun clearCurrentSegment() {
        if (currentSegment != emptySegment) {
            currentSegment = emptySegment
        }
    }

    private fun checkLocationChanged(location: LocationModel): Boolean =
        (lastLocation == null || lastLocation != location)

    private fun updateLastLocationTime(location: LocationModel, timeMillis: Long) {
        lastLocation = location
        lastTimeStamp = timeMillis
    }

    private fun getUpdatedSegment(
        location: LocationModel,
        timeMillis: Long
    ): TrackSegment {
        val preparedSegment = getPreparedSegment()
        return getSegmentWithLocTimeData(preparedSegment, location, timeMillis)
    }

    private fun getPreparedSegment(): TrackSegment {
        val preparedSegment = if (currentSegment.hasStartEndData()) {
            val location = currentSegment.endLocation ?: error("End location cannot be null")
            val time = currentSegment.endTime ?: error("End time cannot be null")
            emptySegment.withStartData(location, time)
        } else {
            currentSegment
        }
        return preparedSegment
    }

    private fun getSegmentWithLocTimeData(
        segment: TrackSegment,
        location: LocationModel,
        timeMillis: Long
    ): TrackSegment {
        return if (!segment.hasStartData()) {
            segment.withStartData(location, timeMillis)
        } else {
            segment.withEndData(location, timeMillis)
        }
    }

    private fun setCurrentSegment(segment: TrackSegment) {
        currentSegment = segment
    }

    private fun getSegmentOrNullIfHasNoStartEndData(segment: TrackSegment): TrackSegment? =
        (if (segment.hasStartEndData()) segment else null).also {
            Log.i(
                "My Stack",
                "Final segment is: $it"
            )
        }

}