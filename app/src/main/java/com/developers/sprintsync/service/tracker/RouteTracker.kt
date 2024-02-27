package com.developers.sprintsync.service.tracker

import com.developers.sprintsync.manager.location.LocationProvider
import com.developers.sprintsync.manager.segment.SegmentBuilder
import com.developers.sprintsync.model.tracking.LocationModel
import com.developers.sprintsync.model.tracking.TrackSegment
import com.developers.sprintsync.model.tracking.toDataModel
import com.developers.sprintsync.util.extension.withLatestFrom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteTracker
    @Inject
    constructor(
        private val locationProvider: LocationProvider,
        private val timeTracker: TimeTracker,
        private val segmentBuilder: SegmentBuilder,
    ) {
        private val _isActive = MutableStateFlow(false)
        val isActive: StateFlow<Boolean>
            get() = _isActive

        init {
            initActiveStateListener()
        }

        fun locationFlow(): Flow<LocationModel> =
            locationProvider.listenToLocation().map { location ->
                location.toDataModel()
            }

        fun timeInMillisFlow(): Flow<Long> = timeTracker.timeInMillisFlow()

        fun trackSegmentFlow(): Flow<TrackSegment> =
            locationFlow().withLatestFrom(timeInMillisFlow()) { location, timeMillis ->
                val isActive = _isActive.value
                if (isActive) {
                    segmentBuilder.nextSegmentOrNull(location, timeMillis)
                } else {
                    null
                }
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
                    if (!isActive) {
                        segmentBuilder.reset()
                    }
                }
            }
        }
    }
