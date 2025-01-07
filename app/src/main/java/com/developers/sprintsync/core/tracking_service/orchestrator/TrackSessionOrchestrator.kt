package com.developers.sprintsync.core.tracking_service.orchestrator

import android.util.Log
import com.developers.sprintsync.core.util.extension.withLatestConcat
import com.developers.sprintsync.core.tracking_service.data.model.session.TrackStatus
import com.developers.sprintsync.core.tracking_service.data.model.session.TrackingSession
import com.developers.sprintsync.core.tracking_service.data.model.location.LocationModel
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.tracking_service.data.processing.track.TrackBuilder
import com.developers.sprintsync.core.tracking_service.activity_monitoring.ActivityMonitor
import com.developers.sprintsync.core.tracking_service.provider.location.LocationProvider
import com.developers.sprintsync.core.tracking_service.provider.time.TimeProvider
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackSessionOrchestrator
    @Inject
    constructor(
        private val locationProvider: LocationProvider,
        private val timeProvider: TimeProvider,
        private val trackBuilder: TrackBuilder,
        private val activityMonitor: ActivityMonitor,
    ) {
        private val _trackSessionOrchestratorState: MutableStateFlow<TrackSessionOrchestratorState> =
            MutableStateFlow(TrackSessionOrchestratorState.Initialised)
        val state = _trackSessionOrchestratorState.asStateFlow()

        val userActivityState = activityMonitor.userActivityState

        private val _data = MutableStateFlow(TrackingSession.DEFAULT)
        val data = _data.asStateFlow()

        private val timeFlow =
            timeProvider.timeInMillisFlow()

        private val locationFlow = locationProvider.listenToLocation()
        private val trackFlow =
            locationFlow.withLatestConcat(timeFlow) { location, timeMillis ->
                if (activityMonitor.userHasStopped()) {
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
        }

        fun startUpdatingLocation() {
            initLocationScope()
            locationScope?.launch(CoroutineName("location")) {
                locationFlow.collect { location ->
                    Log.d("MyStack", "new location")
                    updateSession(location)
                }
            }
        }

        fun stopUpdatingLocation() {
            locationScope?.cancel()
            locationScope = null
        }

        fun start() {
            _trackSessionOrchestratorState.value = TrackSessionOrchestratorState.Tracking
        }

        fun pause() {
            _trackSessionOrchestratorState.value = TrackSessionOrchestratorState.Paused
        }

        fun finish() {
            _trackSessionOrchestratorState.value = TrackSessionOrchestratorState.Finished
        }

        fun reset() {
            resetTrackData()
            resetDuration()
            resetSessionStatus()
            resetState()
            timeProvider.reset()
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

        private fun addInactiveSegmentToTrack() {
            val endPauseTime = data.value.durationMillis
            trackBuilder.addInactiveDataPoint(endPauseTime)
            val track = trackBuilder.buildTrack()
            updateSession(track)
        }

        private suspend fun finaliseTrack() {
            if (activityMonitor.userHasStopped()) {
                addInactiveSegmentToTrack()
            } else {
                finalizeActiveTrack()
            }
        }

        private suspend fun finalizeActiveTrack() {
            val location = locationProvider.getLocation()
            val time = data.value.durationMillis
            trackBuilder.addActiveDataPoint(location, time)
            val track = trackBuilder.buildTrack()
            updateSession(track)
            Log.d("MyStack", "track finalised")
        }

        private fun areUpdatingCoroutinesInactive(): Boolean = (locationScope == null && trackingScope == null && timeScope == null)

        private fun resetTrackData() {
            trackBuilder.reset()
            val track = trackBuilder.buildTrack()
            updateSession(track)
        }

        private fun resetDuration() {
            updateSession(0L)
        }

        private fun resetSessionStatus() {
            updateSession(TrackStatus.Incomplete)
        }

        private fun resetState() {
            _trackSessionOrchestratorState.value = TrackSessionOrchestratorState.Initialised
        }

        private fun completeSession(isTrackValid: Boolean) {
            Log.d("My Stack", "completeSession : $isTrackValid")
            when (isTrackValid) {
                true -> updateSession(TrackStatus.Valid)
                false -> updateSession(TrackStatus.Invalid)
            }
        }

        private fun initTrackerStateListener() {
            CoroutineScope(dispatcher).launch {
                state.collect { state ->
                    when (state) {
                        TrackSessionOrchestratorState.Initialised -> {
                            // NO - OP
                        }

                        TrackSessionOrchestratorState.Tracking -> {
                            startUpdatingTime()
                            startUpdatingTrack()
                        }

                        TrackSessionOrchestratorState.Paused -> {
                            finaliseTrack()
                            stopUpdatingTime()
                            stopUpdatingTrack()
                            trackBuilder.clearLastDataPoint()
                            activityMonitor.stopMonitoringInactivity()
                        }

                        TrackSessionOrchestratorState.Finished -> {
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
