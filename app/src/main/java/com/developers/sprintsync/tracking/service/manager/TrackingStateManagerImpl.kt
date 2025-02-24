package com.developers.sprintsync.tracking.service.manager

import com.developers.sprintsync.tracking.component.model.TrackState
import com.developers.sprintsync.tracking.component.model.TrackingStatus
import com.developers.sprintsync.tracking.data.model.TimedLocation
import com.developers.sprintsync.tracking.data.processing.track.TrackFlowGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingStateManagerImpl
    @Inject
    constructor(
        private val trackFlowGenerator: TrackFlowGenerator,
    ) : TrackingStateManager() {
        private val trackingStatusFlow = MutableStateFlow(TrackingStatus.INITIALIZED)
        private val trackFlow = trackFlowGenerator.trackFlow

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        override val trackingStateFlow: Flow<TrackState> =
            combine(trackingStatusFlow, trackFlow) { status, track ->
                if (status == TrackingStatus.PAUSED) trackFlowGenerator.resetLastTimedLocation()
                TrackState(track, status)
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TrackState.INITIAL,
            )

        override fun updateLocationDuration(data: TimedLocation) {
            trackFlowGenerator.addTimedLocation(data)
        }

        override fun updateTrackingStatus(status: TrackingStatus) {
            scope.launch {
                trackingStatusFlow.emit(status)
            }
        }

        override fun resetState() {
            trackingStatusFlow.update { TrackingStatus.INITIALIZED }
            trackFlowGenerator.resetTrackingData()
        }

        companion object {
            private const val TAG = "My stack: TrackingStateManager"
        }
    }
