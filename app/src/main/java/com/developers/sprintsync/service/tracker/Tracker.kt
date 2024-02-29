package com.developers.sprintsync.service.tracker

import android.util.Log
import com.developers.sprintsync.manager.location.LocationProvider
import com.developers.sprintsync.tracking.model.LocationModel
import com.developers.sprintsync.updater.TrackUpdater
import com.developers.sprintsync.util.extension.withLatestFrom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
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

        val testFlow =
            flow {
                while (true) {
                    Log.i("My stack", "The object is: $this, isActive: ${isActive.value}")
                    if (isActive.value) {
                        val int = (Math.random() * 100).toInt()
                        emit(int)
                        delay(1000)
                    }
                }
            }

        val timeInMillisFlow: Flow<Long> = timeProvider.timeInMillisFlow()

        val locationFlow: Flow<LocationModel> =
            locationProvider.listenToLocation()

        val trackFLow =
            locationFlow.withLatestFrom(timeInMillisFlow) { location, timeMillis ->
                if (isActive.value) {
                    trackUpdater.getTrack(location, timeMillis)
                }
            }

        fun start() {
            _isActive.value = true
        }

        fun stop() {
            _isActive.value = false
        }

        fun reset() {
        }

        private fun initActiveStateListener() {
            CoroutineScope(Dispatchers.IO).launch {
                _isActive.collect { isActive ->
                    timeProvider.updateStopwatchState(isActive)
                    if (!isActive) {
                        trackUpdater.onPause()
                    }
                }
            }
        }
    }
