package com.developers.sprintsync.presentation.workout_session.active

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.domain.track_preview.cropper.TrackPreviewCropper
import com.developers.sprintsync.domain.tracking_service.model.toLatLng
import com.developers.sprintsync.domain.tracking_service.use_case.GetSessionDataUseCase
import com.developers.sprintsync.domain.tracking_service.use_case.GetTrackingDataUseCase
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event.TrackingUiEventHandler
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.map.MapStateHandler
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.snapshot.SnapshotStateHandler
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.ui.TrackingUiStateHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutSessionViewModel
    @Inject
    constructor(
        private val trackingServiceDataFlow: GetTrackingDataUseCase,
        private val trackingSessionDataFlow: GetSessionDataUseCase,
        private val uiEventHandler: TrackingUiEventHandler,
        private val uiStateHandler: TrackingUiStateHandler,
        private val mapStateHandler: MapStateHandler,
        private val snapshotStateHandler: SnapshotStateHandler,
        private val snapshotCropper: TrackPreviewCropper,
    ) : ViewModel() {
        val uiEventFlow = uiEventHandler.uiEventFlow
        val uiStateFlow = uiStateHandler.uiStateFlow
        val mapStateFlow = mapStateHandler.mapStateFlow

        init {
            observeTrackData()
            observeLocationFlow()
        }

        val duration =
            trackingSessionDataFlow
                .invoke()
                .map { DurationUiFormatter.format(it.durationMillis, DurationUiPattern.HH_MM_SS) }

        fun onSnapshotReady(snapshot: Bitmap?) {
            if (snapshot != null) {
                val croppedSnapshot = snapshotCropper.getCroppedBitmap(snapshot)
                snapshotStateHandler.emitSnapshot(croppedSnapshot)
            } else {
                Log.e(TAG, "Snapshot is null")
            }
        }

        private fun observeLocationFlow() {
            viewModelScope.launch {
                trackingSessionDataFlow()
                    .mapNotNull { it.userLocation.toLatLng() }
                    .distinctUntilChanged()
                    .collect { location ->
                        mapStateHandler.emitLocation(location)
                    }
            }
        }

        private fun observeTrackData() {
            viewModelScope.launch {
                trackingServiceDataFlow().collect { state ->
                    uiStateHandler.handleState(state.status)
                    uiEventHandler.handleState(state)
                }
            }
        }

        private companion object {
            const val TAG = "My stack: TrackingSessionViewModel"
        }
    }
