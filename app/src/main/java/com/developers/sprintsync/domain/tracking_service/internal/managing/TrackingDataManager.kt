package com.developers.sprintsync.domain.tracking_service.internal.managing

import com.developers.sprintsync.domain.tracking_service.model.TrackingData
import com.developers.sprintsync.domain.tracking_service.model.TrackingStatus
import com.developers.sprintsync.domain.tracking_service.model.TimedLocation
import com.developers.sprintsync.domain.tracking_service.internal.data_processing.track.TrackFlowGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class TrackingDataManager
    @Inject
    constructor(
        private val trackFlowGenerator: TrackFlowGenerator,
        scope: CoroutineScope,
    ) {
        private val trackingStatusFlow = MutableStateFlow(TrackingStatus.INITIALIZED)
        private val trackFlow = trackFlowGenerator.trackFlow

        val trackingDataFlow: Flow<TrackingData> =
            combine(trackingStatusFlow, trackFlow) { status, track ->
                if (status == TrackingStatus.PAUSED) trackFlowGenerator.resetLastTimedLocation()
                TrackingData(track, status)
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TrackingData.INITIAL,
            )

        fun updateTimedLocation(data: TimedLocation) {
            trackFlowGenerator.addTimedLocation(data)
        }

        fun updateTrackingStatus(status: TrackingStatus) {
            trackingStatusFlow.update { status }
        }

        fun resetState() {
            trackingStatusFlow.update { TrackingStatus.INITIALIZED }
            trackFlowGenerator.resetTrackingData()
        }

        companion object {
            private const val TAG = "My stack: TrackingStateManager"
        }
    }
