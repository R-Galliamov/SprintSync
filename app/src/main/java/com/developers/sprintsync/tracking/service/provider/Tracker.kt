package com.developers.sprintsync.tracking.service.provider

import android.util.Log
import com.developers.sprintsync.global.util.extension.withLatestConcat
import com.developers.sprintsync.tracking.builder.track.TrackUpdater
import com.developers.sprintsync.tracking.model.LocationModel
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.service.provider.location.LocationProvider
import com.developers.sprintsync.tracking.service.provider.time.TimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Tracker
    @Inject
    constructor(
        private val locationProvider: LocationProvider,
        private val timeProvider: TimeProvider,
        private val trackUpdater: TrackUpdater,
    ) {
        private val _isActive = MutableStateFlow(false)
        val isActive = _isActive.asStateFlow()

        init {
            initActiveStateListener()
        }

        val timeInMillisFlow: Flow<Long> = timeProvider.timeInMillisFlow()

        val locationFlow: Flow<LocationModel> =
            locationProvider.listenToLocation()

        val trackFlow: Flow<Track> =
            locationFlow.withLatestConcat(timeInMillisFlow) { location, timeMillis ->
                Log.i("My Stack", "Tracker: location: $location, timeMillis: $timeMillis")
                if (isActive.value)
                    {
                        trackUpdater.getTrack(location, timeMillis)
                    } else {
                    null
                }
            }.filterNotNull().onStart { emit(Track.EMPTY_TRACK_DATA) }

        fun start() {
            updateActiveState(true)
        }

        fun stop() {
            updateActiveState(false)
        }

        private fun updateActiveState(isActive: Boolean) {
            _isActive.value = isActive
        }

        private fun initActiveStateListener() {
            CoroutineScope(Dispatchers.IO).launch {
                isActive.collect { isActive ->
                    timeProvider.updateStopwatchState(isActive)
                    if (!isActive) {
                        trackUpdater.onPause()
                    }
                }
            }
        }
    }
