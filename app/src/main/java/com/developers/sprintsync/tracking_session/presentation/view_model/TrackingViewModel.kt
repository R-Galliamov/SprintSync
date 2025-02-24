package com.developers.sprintsync.tracking_session.presentation.view_model

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationFormatter
import com.developers.sprintsync.core.components.track_snapshot.presentation.util.TrackSnapshotCropper
import com.developers.sprintsync.tracking.component.use_case.GetCurrentTrackingStateUseCase
import com.developers.sprintsync.tracking.component.use_case.GetLocationFlowUseCase
import com.developers.sprintsync.tracking.data.model.toLatLng
import com.developers.sprintsync.tracking.data.provider.time.GetDurationFlowUseCase
import com.developers.sprintsync.tracking_session.presentation.util.state_handler.map.MapStateHandler
import com.developers.sprintsync.tracking_session.presentation.util.state_handler.snapshot.SnapshotStateHandler
import com.developers.sprintsync.tracking_session.presentation.util.state_handler.event.TrackingUiEventHandler
import com.developers.sprintsync.tracking_session.presentation.util.state_handler.ui.TrackingUiStateHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel
    @Inject
    constructor(
        getDurationFlowUseCase: GetDurationFlowUseCase,
        private val getLocationFlowUseCase: GetLocationFlowUseCase,
        private val trackingState: GetCurrentTrackingStateUseCase,
        private val uiEventHandler: TrackingUiEventHandler,
        private val uiStateHandler: TrackingUiStateHandler,
        private val mapStateHandler: MapStateHandler,
        private val snapshotStateHandler: SnapshotStateHandler,
        private val snapshotCropper: TrackSnapshotCropper,
    ) : ViewModel() {
        val uiEventFlow = uiEventHandler.uiEventFlow
        val uiStateFlow = uiStateHandler.uiStateFlow
        val mapStateFlow = mapStateHandler.mapStateFlow

        init {
            observeTrackData()
            observeLocationFlow()
        }

        val duration = getDurationFlowUseCase().map { DurationFormatter.formatToHhMmSs(it) }

        fun onSnapshotReady(snapshot: Bitmap?) {
            if (snapshot != null) {
                val croppedSnapshot = snapshotCropper.getCroppedSnapshot(snapshot)
                snapshotStateHandler.emitSnapshot(croppedSnapshot)
            } else {
                Log.e(TAG, "Snapshot is null")
            }
        }

        private fun observeLocationFlow() {
            viewModelScope.launch {
                getLocationFlowUseCase()
                    .map { it.toLatLng() }
                    .distinctUntilChanged()
                    .collect { location ->
                        mapStateHandler.emitLocation(location)
                    }
            }
        }

        private fun observeTrackData() {
            viewModelScope.launch {
                trackingState().collect { state ->
                    uiStateHandler.handleState(state.status)
                    uiEventHandler.handleState(state)
                }
            }
        }

        private companion object {
            const val TAG = "My stack: TrackingSessionViewModel"
        }
    }
