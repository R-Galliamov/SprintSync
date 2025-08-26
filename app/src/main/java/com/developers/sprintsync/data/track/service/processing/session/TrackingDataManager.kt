package com.developers.sprintsync.data.track.service.processing.session

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.processing.track.TrackGenerator
import com.developers.sprintsync.domain.track.model.TrackingData
import com.developers.sprintsync.domain.track.model.TrackingStatus
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Manages tracking data and status updates
@ServiceScoped
class TrackingDataManager
@Inject
constructor(
    private val trackGenerator: TrackGenerator,
    scope: CoroutineScope,
    private val log: AppLogger
) {
    private val trackingStatusFlow = MutableStateFlow<TrackingStatus>(TrackingStatus.Initialized)
    private val trackFlow = trackGenerator.trackFlow

    // Combines tracking status and track data into a single flow
    val trackingDataFlow: StateFlow<TrackingData> =
        combine(trackingStatusFlow, trackFlow) { status, track ->
            if (status is TrackingStatus.Paused) trackGenerator.resetLastTimedLocation()
            TrackingData(track, status)
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TrackingData.INITIAL,
        )

    // Updates track with new timed location
    fun updateTimedLocation(data: TrackPoint) = trackGenerator.addTimedLocation(data)

    // Updates tracking status
    fun updateTrackingStatus(status: TrackingStatus) {
        trackingStatusFlow.update { status }
        log.i("Tracking status updated to: $status")
    }

    fun resetTracking() {
        trackGenerator.resetTrack()
    }
}
