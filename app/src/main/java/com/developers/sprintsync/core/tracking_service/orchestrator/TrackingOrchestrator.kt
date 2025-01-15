package com.developers.sprintsync.core.tracking_service.orchestrator

import android.util.Log
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.tracking_service.activity_monitoring.ActivityMonitor
import com.developers.sprintsync.core.tracking_service.data.model.location.LocationModel
import com.developers.sprintsync.core.tracking_service.data.model.session.TrackStatus
import com.developers.sprintsync.core.tracking_service.data.model.session.TrackingSession
import com.developers.sprintsync.core.tracking_service.data.processing.segment.SegmentType
import com.developers.sprintsync.core.tracking_service.data.processing.track.TrackGenerator
import com.developers.sprintsync.core.tracking_service.provider.location.LocationProvider
import com.developers.sprintsync.core.tracking_service.provider.time.TimeProvider
import com.developers.sprintsync.core.util.extension.withLatestConcat
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingOrchestrator
    @Inject
    constructor(
        private val locationProvider: LocationProvider,
        private val timeProvider: TimeProvider,
        private val trackGenerator: TrackGenerator,
        private val activityMonitor: ActivityMonitor,
    ) {
        private val _sessionState: MutableStateFlow<TrackingState> =
            MutableStateFlow(TrackingState.Initialised)
        val state = _sessionState.asStateFlow()

        val userActivityState = activityMonitor.userActivityState

        private val _data = MutableStateFlow(TrackingSession.DEFAULT)
        val data = _data.asStateFlow()

        private val timeFlow =
            timeProvider.timeInMillisFlow()

        private val locationFlow =
            locationProvider.listenToLocation().withLatestConcat(timeFlow) { location, timeMillis ->
                updateTrackData(activityMonitor.userHasStopped(), location, timeMillis)
                location
            }

        private val trackFlow = trackGenerator.trackFlow

        private var locationScope: CoroutineScope? = null
        private var trackingScope: CoroutineScope? = null
        private var timeScope: CoroutineScope? = null

        private val dispatcher = Dispatchers.IO

        init {
            observeStateChanges()
        }

        fun startUpdatingLocation() {
            initLocationScope()
            locationScope?.launch(CoroutineName("location")) {
                locationFlow.collect { location ->
                    Log.d("My Stack", "Tracker: new location")
                    updateSession(location)
                }
            }
        }

        fun stopUpdatingLocation() {
            locationScope?.cancel()
            locationScope = null
        }

        fun start() {
            _sessionState.value = TrackingState.Tracking
        }

        fun pause() {
            _sessionState.value = TrackingState.Paused
        }

        fun finish() {
            _sessionState.value = TrackingState.Finished
        }

        fun reset() {
            resetTrackData()
            resetDuration()
            resetSessionStatus()
            resetState()
            timeProvider.reset()
        }

        private fun updateTrackData(
            userHasStopped: Boolean,
            location: LocationModel,
            timeMillis: Long,
        ) {
            if (userHasStopped) {
                trackGenerator.addDataPoint(SegmentType.INACTIVE, location, timeMillis)
            }
            trackGenerator.addDataPoint(SegmentType.ACTIVE, location, timeMillis)
        }

        private fun isTrackValid(): Boolean = getTrack().segments.isNotEmpty()

        private fun initTimeScope() {
            timeScope = CoroutineScope(dispatcher)
        }

        private fun initTrackingScope() {
            trackingScope = CoroutineScope(dispatcher)
        }

        private fun initLocationScope() {
            locationScope = CoroutineScope(dispatcher)
        }

        private fun startUpdatingTime() {
            initTimeScope()
            timeProvider.updateStopwatchState(true)
            timeScope?.launch(CoroutineName("time")) {
                timeFlow.collect { time ->
                    Log.d("My stack", "Tracker: new time: $time")
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
                    Log.d("My stack", "Tracker: new track: $track")
                }
            }
        }

        private fun stopUpdatingTime() {
            timeProvider.updateStopwatchState(false)
            timeScope?.cancel()
            timeScope = null
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

        private fun updateSession(trackStatus: TrackStatus) {
            _data.value = _data.value.copy(trackStatus = trackStatus)
        }

        private fun getTrack(): Track = data.value.track

        private suspend fun finaliseTrack() {
            val segmentType =
                if (activityMonitor.userHasStopped()) {
                    SegmentType.INACTIVE
                } else {
                    SegmentType.ACTIVE
                }
            val location = locationProvider.getLocation()
            val time = data.value.durationMillis
            trackGenerator.addDataPoint(segmentType, location, time)
            val track = trackGenerator.track
            updateSession(track)
            Log.d("MyStack", "track finalised")
        }

        private fun areUpdatingCoroutinesInactive(): Boolean = (locationScope == null && trackingScope == null && timeScope == null)

        private fun resetTrackData() {
            trackGenerator.reset()
            val track = trackGenerator.track
            updateSession(track)
        }

        private fun resetDuration() {
            updateSession(0L)
        }

        private fun resetSessionStatus() {
            updateSession(TrackStatus.Incomplete)
        }

        private fun resetState() {
            _sessionState.value = TrackingState.Initialised
        }

        private fun completeSession(isTrackValid: Boolean) {
            Log.d("My Stack", "completeSession : $isTrackValid")
            when (isTrackValid) {
                true -> updateSession(TrackStatus.Valid)
                false -> updateSession(TrackStatus.Invalid)
            }
        }

        private fun observeStateChanges() {
            CoroutineScope(dispatcher).launch {
                state.collect { state ->
                    when (state) {
                        TrackingState.Initialised -> {
                            // NO - OP
                        }

                        TrackingState.Tracking -> {
                            startUpdatingTime()
                            startUpdatingTrack()
                        }

                        TrackingState.Paused -> {
                            finaliseTrack()
                            stopUpdatingTime()
                            stopUpdatingTrack()
                            trackGenerator.clearLastDataPoint()
                            activityMonitor.stopMonitoringInactivity()
                        }

                        TrackingState.Finished -> {
                            Log.d("My Stack", "finishing : ${getTrack()}")
                            if (!areUpdatingCoroutinesInactive()) {
                                stopUpdatingLocation()
                                stopUpdatingTime()
                                stopUpdatingTrack()
                                if (isTrackValid()) {
                                    finaliseTrack()
                                }
                            }
                            Log.d("My Stack", "finished : ${getTrack()}")
                            completeSession(isTrackValid())
                            activityMonitor.stopMonitoringInactivity()
                        }
                    }
                }
            }
        }
    }
