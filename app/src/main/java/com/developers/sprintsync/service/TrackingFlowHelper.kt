package com.developers.sprintsync.service

import com.developers.sprintsync.manager.segment.TrackSegmentManager
import com.developers.sprintsync.service.tracker.LocationInTimeTracker
import com.developers.sprintsync.service.tracker.TimeTracker
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
    private val locationTimeTracker: LocationInTimeTracker,
    private val timeTracker: TimeTracker,
    private val segmentManager: TrackSegmentManager,
) {

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean>
        get() = _isActive

    init {
        initActiveStateListener()
    }

    fun locationFlow() = locationTimeTracker.locationFlow()

    fun timeInMillisFlow(): Flow<Long> = timeTracker.timeInMillisFlow()

    fun trackSegmentFlow() =
        locationFlow().combine(timeInMillisFlow()) { location, timeMillis ->
            location to timeMillis
        }.map { pair ->
            val location = pair.first
            val time = pair.second
            val isActive = _isActive.value
            if (isActive && locationTimeTracker.assureLocationChanged(location)) {
                locationTimeTracker.updateLastLocationTime(location, time)
                segmentManager.nextSegmentOrNull(location, time)
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
                timeTracker.updateStopwatchState(isActive)
                segmentManager.clear()
            }
        }
    }
}
