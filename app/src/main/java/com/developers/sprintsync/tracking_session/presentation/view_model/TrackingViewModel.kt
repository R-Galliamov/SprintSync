package com.developers.sprintsync.tracking_session.presentation.view_model

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationFormatter
import com.developers.sprintsync.core.components.track_snapshot.presentation.util.TrackSnapshotCropper
import com.developers.sprintsync.tracking.data.model.toLatLng
import com.developers.sprintsync.tracking.component.use_case.GetCurrentTrackingStateUseCase
import com.developers.sprintsync.tracking.component.use_case.GetLocationFlowUseCase
import com.developers.sprintsync.tracking.data.provider.time.GetDurationFlowUseCase
import com.developers.sprintsync.tracking_session.presentation.util.state_handler.SnapshotStateHandler
import com.developers.sprintsync.tracking_session.presentation.util.state_handler.TrackingUiEventHandler
import com.developers.sprintsync.tracking_session.presentation.util.state_handler.TrackingUiStateHandler
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel
    @Inject
    constructor(
        getDurationFlowUseCase: GetDurationFlowUseCase,
        getLocationFlowUseCase: GetLocationFlowUseCase,
        private val trackingState: GetCurrentTrackingStateUseCase,
        private val eventHandler: TrackingUiEventHandler,
        private val stateHandler: TrackingUiStateHandler,
        private val snapshotStateHandler: SnapshotStateHandler,
        private val snapshotCropper: TrackSnapshotCropper,
    ) : ViewModel() {
        val uiEventFlow = eventHandler.uiEventFlow
        val uiStateFlow = stateHandler.uiStateFlow

        init {
            observeTrackData()
        }

        val duration = getDurationFlowUseCase().map { DurationFormatter.formatToHhMmSs(it) }

        val userLocation: Flow<LatLng> =
            getLocationFlowUseCase()
                .map { it.toLatLng() }
                .distinctUntilChanged()

        fun onSnapshotReady(snapshot: Bitmap?) {
            if (snapshot != null) {
                val croppedSnapshot = snapshotCropper.getCroppedSnapshot(snapshot)
                snapshotStateHandler.emitSnapshot(croppedSnapshot)
            } else {
                Log.e(TAG, "Snapshot is null")
            }
        }

        private fun observeTrackData() {
            viewModelScope.launch {
                trackingState().collect { state ->
                    stateHandler.handleState(state.status)
                    eventHandler.handleState(state)
                }
            }
        }

        private companion object {
            const val TAG = "My stack: TrackingSessionViewModel"
        }
    }
