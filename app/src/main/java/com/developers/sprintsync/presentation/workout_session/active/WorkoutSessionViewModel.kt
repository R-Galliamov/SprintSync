package com.developers.sprintsync.presentation.workout_session.active

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.data.track_preview.cropper.BitmapCropper
import com.developers.sprintsync.data.track_preview.cropper.TrackPreviewDimensions
import com.developers.sprintsync.domain.track.model.SessionData
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.model.TrackingData
import com.developers.sprintsync.domain.track.model.toLatLng
import com.developers.sprintsync.presentation.workout_session.active.util.polyline.PolylineProcessor
import com.developers.sprintsync.presentation.workout_session.active.util.segments.SegmentsTracker
import com.developers.sprintsync.presentation.workout_session.active.util.service.ServiceConnectionResult
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event.UiEventHandler
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.map.MapStateHandler
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.snapshot.SnapshotStateHandler
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.ui.UiStateHandler
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutSessionViewModel
@Inject
constructor(
    private val uiEventHandler: UiEventHandler,
    private val uiStateHandler: UiStateHandler,
    private val mapStateHandler: MapStateHandler,
    private val snapshotStateHandler: SnapshotStateHandler,
    private val trackPreviewDimensions: TrackPreviewDimensions,
    private val segmentsTracker: SegmentsTracker,
    private val polylineProcessor: PolylineProcessor,
) : ViewModel() {
    val uiEventFlow = uiEventHandler.uiEventFlow
    val uiStateFlow = uiStateHandler.uiStateFlow
    val mapStateFlow = mapStateHandler.mapStateFlow

    val durationFlow = uiStateHandler.durationFlow
    val metricsFlow = uiStateHandler.metricsFlow

    fun bindTo(connectionResult: ServiceConnectionResult) {
        when (connectionResult) {
            is ServiceConnectionResult.Success ->
                observeWorkoutSessionData(
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

    private fun observeWorkoutSessionData(
        sessionDataFlow: Flow<SessionData>,
        trackingDataFlow: Flow<TrackingData>,
    ) {
        viewModelScope.launch {
            sessionDataFlow.collect { data ->
                data.userLocation?.toLatLng()?.let { mapStateHandler.emitLocation(it) }
                data.durationMillis.let { uiStateHandler.handleDuration(it) }
            }
        }
        viewModelScope.launch {
            trackingDataFlow.collect { data ->
                val polylines = retrievePolylines(data.track)
                mapStateHandler.emitPolylines(polylines)
                uiStateHandler.handleStatus(data.status)
                uiStateHandler.handleTrack(data.track)
                uiEventHandler.handleState(data)
            }
        }
    }

    private fun retrievePolylines(track: Track): List<PolylineOptions> {
        val segments = segmentsTracker.getNewSegmentsAndAdd(track.segments)
        return polylineProcessor.generatePolylines(segments)
    }

    private companion object {
        const val TAG = "My stack: TrackingSessionViewModel"
    }
}
