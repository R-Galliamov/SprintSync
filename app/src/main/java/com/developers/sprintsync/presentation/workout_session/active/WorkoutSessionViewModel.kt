package com.developers.sprintsync.presentation.workout_session.active

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.data.track_preview.cropper.BitmapCropper
import com.developers.sprintsync.data.track_preview.cropper.TrackPreviewDimensions
import com.developers.sprintsync.domain.track.model.SessionData
import com.developers.sprintsync.domain.track.model.TrackingData
import com.developers.sprintsync.domain.track.model.toLatLng
import com.developers.sprintsync.presentation.workout_session.active.util.service.ServiceConnectionResult
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event.TrackingUiEventHandler
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.map.MapStateHandler
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.snapshot.SnapshotStateHandler
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.ui.TrackingUiStateHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutSessionViewModel
    @Inject
    constructor(
        private val uiEventHandler: TrackingUiEventHandler,
        private val uiStateHandler: TrackingUiStateHandler,
        private val mapStateHandler: MapStateHandler,
        private val snapshotStateHandler: SnapshotStateHandler,
        private val trackPreviewDimensions: TrackPreviewDimensions,
    ) : ViewModel() {
        val uiEventFlow = uiEventHandler.uiEventFlow
        val uiStateFlow = uiStateHandler.uiStateFlow
        val mapStateFlow = mapStateHandler.mapStateFlow

        private val _durationFlow = MutableStateFlow(DurationUiFormatter.format(0L, DurationUiPattern.HH_MM_SS))
        val durationFlow: StateFlow<String> = _durationFlow.asStateFlow()

        fun bindTo(connectionResult: ServiceConnectionResult) {
            when (connectionResult) {
                is ServiceConnectionResult.Success ->
                    observeSessionData(
                        connectionResult.dataHolder.sessionDataFlow,
                        connectionResult.dataHolder.trackingDataFlow,
                    )

                is ServiceConnectionResult.Failure -> uiEventHandler.onError(connectionResult.e)
            }
        }

        fun onSnapshotReady(snapshot: Bitmap?) {
            if (snapshot != null) {
                val croppedSnapshot =
                    BitmapCropper.cropToTargetDimensions(
                        snapshot,
                        trackPreviewDimensions.width,
                        trackPreviewDimensions.height,
                    )
                snapshotStateHandler.emitSnapshot(croppedSnapshot)
            } else {
                Log.e(TAG, "Snapshot is null")
            }
        }

        private fun observeSessionData(
            sessionDataFlow: Flow<SessionData>,
            trackingDataFlow: Flow<TrackingData>,
        ) {
            viewModelScope.launch {
                sessionDataFlow.collect { data ->
                    data.userLocation?.toLatLng()?.let { mapStateHandler.emitLocation(it) }
                    data.durationMillis.let {
                        _durationFlow.emit(
                            DurationUiFormatter.format(
                                it,
                                DurationUiPattern.HH_MM_SS,
                            ),
                        )
                    }
                }
            }
            viewModelScope.launch {
                trackingDataFlow.collect { state ->
                    Log.i(TAG, state.toString())
                    uiStateHandler.handleState(state.status)
                    uiEventHandler.handleState(state)
                }
            }
        }

        private companion object {
            const val TAG = "My stack: TrackingSessionViewModel"
        }
    }
