package com.developers.sprintsync.data.track.service.processing.session

import com.developers.sprintsync.core.util.extension.withLatestConcat
import com.developers.sprintsync.data.track.service.provider.DurationProvider
import com.developers.sprintsync.data.track.service.provider.LocationProvider
import com.developers.sprintsync.domain.track.model.SessionData
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ServiceScoped
class SessionManager
    @Inject
    constructor(
        private val locationProvider: LocationProvider,
        private val durationProvider: DurationProvider,
        private val scope: CoroutineScope,
    ) {
        private var isRunning: Boolean = false
        private var job: Job? = null

        val sessionDataFlow =
            locationProvider.locationFlow
                .combine(durationProvider.durationMillisFlow) { location, duration ->
                    SessionData(location, duration)
                }.stateIn(
                    scope = scope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = SessionData.INITIAL,
                )

        fun launchLocationUpdates() = locationProvider.start()

        fun start(onNewTimedLocation: (timedLocation: TimedLocation) -> Unit) {
            if (isRunning) return

            job?.cancel()
            if (!durationProvider.isRunning) durationProvider.start()
            if (!locationProvider.isRunning) locationProvider.start()
            isRunning = true

            job =
                scope.launch {
                    try {
                        locationProvider.locationFlow
                            .withLatestConcat(durationProvider.durationMillisFlow) { location, duration ->
                                TimedLocation(location, duration)
                            }.collect { timedLocation ->
                                if (isRunning) {
                                    onNewTimedLocation(timedLocation)
                                }
                            }
                    } catch (e: Exception) {
                        println("Error in TimedLocationProvider: ${e.message}")
                        throw e
                    }
                }
        }

        fun pause() {
            if (!isRunning) return

            job?.cancel()
            job = null
            isRunning = false

            durationProvider.pause()
        }

        fun stop() {
            if (!isRunning) return

            job?.cancel()
            job = null
            isRunning = false

            locationProvider.stop()
            durationProvider.reset()
        }
    }
