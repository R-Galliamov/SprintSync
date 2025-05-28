package com.developers.sprintsync.presentation.workout_session.active

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track_preview.util.cropper.BitmapCropper
import com.developers.sprintsync.data.track_preview.util.cropper.TrackPreviewDimensions
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

/**
 * ViewModel for managing the active workout session UI and data.
 */
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
    private val bitmapCropper: BitmapCropper,
    private val log: AppLogger,
) : ViewModel() {
    val uiEventFlow = uiEventHandler.uiEventFlow
    val uiStateFlow = uiStateHandler.uiStateFlow
    val mapStateFlow = mapStateHandler.mapStateFlow

    val durationFlow = uiStateHandler.durationFlow
    val metricsFlow = uiStateHandler.metricsFlow

    /**
     * Binds the ViewModel to a service connection result to observe workout session data.
     * @param connectionResult The result of the service connection attempt.
     */
    fun bindTo(connectionResult: ServiceConnectionResult) {
        when (connectionResult) {
            is ServiceConnectionResult.Success -> {
                log.i("Service connection successful, observing session data")
                observeWorkoutSessionData(
                    connectionResult.dataHolder.sessionDataFlow,
                    connectionResult.dataHolder.trackingDataFlow,
                )
            }

            is ServiceConnectionResult.Failure -> {
                log.e("Service connection failed: ${connectionResult.e.message}", connectionResult.e)
                uiEventHandler.onError(connectionResult.e)
            }
        }
    }

    /**
     * Processes a map snapshot for the track preview.
     * @param snapshot The captured [Bitmap] snapshot, or null if capture failed.
     */
    fun onSnapshotReady(snapshot: Bitmap?) {
        if (snapshot != null) {
            val croppedSnapshot =
                bitmapCropper.cropToTargetDimensions(
                    snapshot,
                    trackPreviewDimensions.width,
                    trackPreviewDimensions.height,
                )
            snapshotStateHandler.emitSnapshot(croppedSnapshot)
            log.i("Snapshot processed: width=${croppedSnapshot.width}, height=${croppedSnapshot.height}")
        } else {
            log.e("Snapshot is null")
        }
    }

    // Observes session and tracking data flows to update UI and map states
    private fun observeWorkoutSessionData(
        sessionDataFlow: Flow<SessionData>,
        trackingDataFlow: Flow<TrackingData>,
    ) {
        viewModelScope.launch {
            sessionDataFlow.collect { data ->
                data.userLocation?.toLatLng()?.let { latLng ->
                    mapStateHandler.emitLocation(latLng)
                    log.d("Emitted location: $latLng")
                }
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
                log.d("Processed tracking data: status=${data.status}, segments=${data.track.segments.size}")
            }
        }
    }

    private fun retrievePolylines(track: Track): List<PolylineOptions> {
        val segments = segmentsTracker.getNewSegmentsAndAdd(track.segments)
        val polylines = polylineProcessor.generatePolylines(segments)
        log.i("Generated ${polylines.size} polylines for ${segments.size} segments")
        return polylines
    }
}
