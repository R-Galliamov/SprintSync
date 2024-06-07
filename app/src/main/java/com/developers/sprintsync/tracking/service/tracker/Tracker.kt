package com.developers.sprintsync.tracking.service.tracker

import android.util.Log
import com.developers.sprintsync.global.util.extension.withLatestConcat
import com.developers.sprintsync.tracking.model.LocationModel
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.model.TrackerState
import com.developers.sprintsync.tracking.model.TrackingSession
import com.developers.sprintsync.tracking.service.builder.track.TrackBuilder
import com.developers.sprintsync.tracking.service.provider.location.LocationProvider
import com.developers.sprintsync.tracking.service.provider.time.TimeProvider
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class Tracker
    @Inject
    constructor(
        private val locationProvider: LocationProvider,
        private val timeProvider: TimeProvider,
        private val trackBuilder: TrackBuilder,
        private val activityMonitor: ActivityMonitor,
    ) {
        private val _state: MutableStateFlow<TrackerState> = MutableStateFlow(TrackerState.Initialised)
        val state = _state.asStateFlow()

        val userActivityState = activityMonitor.userActivityState

        private val _data = MutableStateFlow(TrackingSession.DEFAULT)
        val data = _data.asStateFlow()

        private val timeFlow = timeProvider.timeInMillisFlow()
        private val locationFlow = locationProvider.listenToLocation()
        private val trackFlow =
            locationFlow.withLatestConcat(timeFlow) { location, timeMillis ->
                Log.d("My Stack", "location: ${location.latitude}, timeMillis: $timeMillis")
                if (activityMonitor.isStopped()) {
                    trackBuilder.addInactiveDataPoint(timeMillis)
                }
                trackBuilder.addActiveDataPoint(location, timeMillis)
                val track = trackBuilder.buildTrack()
                track
            }

        private var locationScope: CoroutineScope? = null
        private var trackingScope: CoroutineScope? = null
        private var timeScope: CoroutineScope? = null

        private val dispatcher = Dispatchers.IO

        init {
            initTrackerStateListener()
            startUpdatingLocation()
        }

        private fun initTimeScope() {
            timeScope = CoroutineScope(dispatcher)
        }

        private fun initTrackingScope() {
            trackingScope = CoroutineScope(dispatcher)
        }

        private fun initLocationScope() {
            locationScope = CoroutineScope(dispatcher)
        }

        private fun startUpdatingLocation() {
            initLocationScope()
            locationScope?.launch(CoroutineName("location")) {
                locationFlow.collect { location ->
                    updateSession(location)
                }
            }
        }

        private fun startUpdatingTime() {
            initTimeScope()
            timeProvider.updateStopwatchState(true)
            timeScope?.launch(CoroutineName("time")) {
                timeFlow.collect { time ->
                    updateSession(time)
                }
            }
        }

        private fun startUpdatingTrack() {
            initTrackingScope()
            trackingScope?.launch(CoroutineName("tracking")) {
                trackFlow.collect { track ->
                    activityMonitor.startMonitoringInactivity()
                    activityMonitor.updateState(track.segments)
                    updateSession(track)
                }
            }
        }

        private fun stopUpdatingTime() {
            timeProvider.updateStopwatchState(false)
            timeScope?.cancel()
            timeScope = null
        }

        private fun stopUpdatingLocation() {
            locationScope?.cancel()
            locationScope = null
        }

        private fun stopUpdatingTrack() {
            trackingScope?.cancel()
            trackingScope = null
        }

        private fun updateSession(location: LocationModel) {
            _data.value = _data.value.copy(currentLocation = location)
        }

        private fun updateSession(durationMillis: Long) {
            _data.value = _data.value.copy(durationMillis = durationMillis)
        }

        private fun updateSession(track: Track) {
            _data.value = _data.value.copy(track = track)
        }

        fun start() {
            _state.value = TrackerState.Tracking
        }

        fun pause() {
            _state.value = TrackerState.Paused
        }

        fun finish() {
            _state.value = TrackerState.Finished
        }

        private fun addInactiveSegmentToTrack() {
            val endPauseTime = data.value.durationMillis
            trackBuilder.addInactiveDataPoint(endPauseTime)
            val track = trackBuilder.buildTrack()
            updateSession(track)
        }

        private fun finaliseTrack() {
            if (activityMonitor.isStopped()) {
                addInactiveSegmentToTrack()
            } else {
                val time = data.value.durationMillis
                CoroutineScope(dispatcher).launch {
                    val location = locationProvider.getLocation()
                    trackBuilder.addActiveDataPoint(location, time)
                    val track = trackBuilder.buildTrack()
                    updateSession(track)
                }
            }
        }

        private fun initTrackerStateListener() {
            CoroutineScope(dispatcher).launch {
                state.collect { state ->
                    when (state) {
                        TrackerState.Initialised -> {
                            // NO - OP
                        }

                        TrackerState.Tracking -> {
                            startUpdatingTime()
                            startUpdatingTrack()
                        }

                        TrackerState.Paused -> {
                            stopUpdatingTime()
                            stopUpdatingTrack()
                            trackBuilder.clearLastDataPoint()
                            activityMonitor.stopMonitoringInactivity()
                            finaliseTrack()
                        }

                        TrackerState.Finished -> {
                            finaliseTrack()
                            stopUpdatingLocation()
                            stopUpdatingTime()
                            stopUpdatingTrack()
                            activityMonitor.stopMonitoringInactivity()
                            _state.value = TrackerState.Initialised
                        }
                    }
                }
            }
        }
    }
