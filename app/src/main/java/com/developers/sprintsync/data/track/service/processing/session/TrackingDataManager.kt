package com.developers.sprintsync.data.track.service.processing.session

import com.developers.sprintsync.data.track.service.processing.track.TrackGenerator
import com.developers.sprintsync.domain.track.model.TrackingData
import com.developers.sprintsync.domain.track.model.TrackingStatus
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@ServiceScoped
class TrackingDataManager
    @Inject
    constructor(
        private val trackGenerator: TrackGenerator,
        scope: CoroutineScope,
    ) {
        private val trackingStatusFlow = MutableStateFlow(TrackingStatus.INITIALIZED)
        private val trackFlow = trackGenerator.trackFlow

        val trackingDataFlow: Flow<TrackingData> =
            combine(trackingStatusFlow, trackFlow) { status, track ->
                if (status == TrackingStatus.PAUSED) trackGenerator.resetLastTimedLocation()
                TrackingData(track, status)
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TrackingData.INITIAL,
            )

        fun updateTimedLocation(data: TimedLocation) {
            trackGenerator.addTimedLocation(data)
        }

        fun updateTrackingStatus(status: TrackingStatus) {
            trackingStatusFlow.update { status }
        }

        fun resetState() {
            trackingStatusFlow.update { TrackingStatus.INITIALIZED }
            trackGenerator.resetTrackingData()
        }

        companion object {
            private const val TAG = "My stack: TrackingStateManager"
        }
    }
