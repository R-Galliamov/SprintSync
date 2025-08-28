package com.developers.sprintsync.data.track.service.processing.session

import com.developers.sprintsync.core.util.extension.withLatestConcat
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.provider.DurationProvider
import com.developers.sprintsync.data.track.service.provider.LocationProvider
import com.developers.sprintsync.domain.track.model.SessionData
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the lifecycle of a session, including tracking location and duration.
 *
 * This class is responsible for:
 * - Starting and stopping location updates.
 * - Starting, pausing, and resetting the session duration.
 * - Combining location and duration data into a single flow (`sessionDataFlow`).
 * - Collecting and processing timed location updates.
 *
 * It uses a [LocationProvider] to get location data and a [DurationProvider]
 * to track the session duration. It operates within a provided [CoroutineScope].
 *
 * @property locationProvider The provider for location data.
 * @property durationProvider The provider for session duration.
 * @property scope The coroutine scope for managing background tasks.
 * @property log The application logger for logging events and errors.
 */
@ServiceScoped
class SessionManager
@Inject
constructor(
    private val locationProvider: LocationProvider,
    private val durationProvider: DurationProvider,
    private val scope: CoroutineScope,
    private val log: AppLogger,
) {
    private var isRunning: Boolean = false
    private var job: Job? = null

    init {
        log.i("SessionManager initialized. HashCode: ${this.hashCode()}")
    }

    // Combines location and duration into a session data flow
    private val _sessionDataFlow: StateFlow<SessionData> =
        locationProvider.locationFlow
            .combine(durationProvider.durationMillisFlow) { location, duration ->
                SessionData(location, duration)
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SessionData.INITIAL,
            )
    val sessionDataFlow: StateFlow<SessionData> = _sessionDataFlow

    // Starts location updates
    fun launchLocationUpdates() = locationProvider.start()

    // Stops location updates
    fun stopLocationUpdates() = locationProvider.stop()

    // Begins tracking session data
    fun start(onNewTimedLocation: (trackPoint: TrackPoint) -> Unit) {
        if (isRunning) {
            log.i("Session already running, skipping start")
            return
        }

        job?.cancel()
        if (!durationProvider.isRunning) durationProvider.start()
        if (!locationProvider.isRunning) locationProvider.start()
        isRunning = true
        log.i("Session started")

        job = startTimedLocationCollectionJob(onNewTimedLocation)
    }

    // Pauses tracking session data
    fun stop() {
        if (!isRunning) {
            log.i("Session not running, skipping pause")
            return
        }
        cancelJob()

        durationProvider.pause()
        log.i("Session paused")
    }

    // Stops tracking session data
    fun resetSession() {
        cancelJob()
        locationProvider.stop()
        durationProvider.reset()
        log.i("Session stopped and reset")
    }

    private fun cancelJob() {
        job?.cancel()
        job = null
        isRunning = false
    }

    // Starts a coroutine job to collect and process location and duration data
    private fun startTimedLocationCollectionJob(onNewTimedLocation: (trackPoint: TrackPoint) -> Unit): Job =
        scope.launch {
            try {
                locationProvider.locationFlow
                    .withLatestConcat(durationProvider.durationMillisFlow) { location, duration ->
                        TrackPoint(location, duration)
                    }.collect { timedLocation ->
                        if (isRunning) {
                            log.i("New timed location: $timedLocation")
                            onNewTimedLocation(timedLocation)
                        }
                    }
            } catch (e: Exception) {
                log.e("Error in TimedLocationProvider: ${e.message}", e)
                throw e
            }
        }
}
