package com.developers.sprintsync.presentation.workout_session.active

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.TrackingServiceDataHolder
import com.developers.sprintsync.data.track_preview.util.cropper.BitmapCropper
import com.developers.sprintsync.data.track_preview.util.cropper.TrackPreviewDimensions
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.SessionData
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
import kotlinx.coroutines.Job
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
    private val polylineProcessor: PolylineProcessor,
    private val segmentsTracker: SegmentsTracker,
    private val bitmapCropper: BitmapCropper,
    private val log: AppLogger,
) : ViewModel() {

    // Crucially, cancel any previous ongoing observations
    // that were started by a previous call to bindTo (e.g., from an old fragment instance)
    private var serviceObservationsJob: Job? = null

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
                val dataHolder = connectionResult.dataHolder
                resetServiceObservations(dataHolder)
                val segments = dataHolder.trackingDataFlow.value.track.segments
                val polylines = getWholePathPolylines(segments)
                mapStateHandler.emitPolylines(polylines)

                log.d("Service connected. Setting up new observations. DataHolder: ${dataHolder.hashCode()}")
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

    private fun resetServiceObservations(dataHolder: TrackingServiceDataHolder) {
        serviceObservationsJob?.cancel()
        serviceObservationsJob = null

        serviceObservationsJob = viewModelScope.launch {
            launch {
                log.d("Starting collection of sessionDataFlow")
                collectSessionDataFlow(dataHolder.sessionDataFlow)
                log.d("Finished collection of sessionDataFlow")
            }

            launch {
                log.d("Starting collection of trackingDataFlow")
                collectTrackingDataFlow(dataHolder.trackingDataFlow)
                log.d("Finished collection of trackingDataFlow")
            }
        }
    }

    // Observes session data flow to update UI and map states
    private suspend fun collectSessionDataFlow(sessionDataFlow: Flow<SessionData>) {
        sessionDataFlow.collect { data ->
            data.userLocation?.toLatLng()?.let { latLng ->
                mapStateHandler.emitLocation(latLng)
            }
            data.durationMillis.let { uiStateHandler.handleDuration(it) }
        }
    }

    // Observes tracking data flow to update UI and map states
    private suspend fun collectTrackingDataFlow(trackingDataFlow: Flow<TrackingData>) {
        trackingDataFlow.collect { data ->
            val polylines = getUnprocessedPathPolylines(data.track.segments)
            mapStateHandler.emitPolylines(polylines)
            uiStateHandler.handleStatus(data.status)
            uiStateHandler.handleTrack(data.track)
            uiEventHandler.handleState(data)
            log.d("Processed tracking data: status=${data.status}, segments=${data.track.segments.size}")
        }
    }

    // Generates polylines for unprocessed segments
    private fun getUnprocessedPathPolylines(segments: List<Segment>): List<PolylineOptions> {
        if (segments.isEmpty()) return emptyList()
        val unprocessedSegments = segmentsTracker.getUnprocessedSegments(segments)
        segmentsTracker.markSegmentsAsProcessed(unprocessedSegments)
        val newPolylines = polylineProcessor.generatePolylines(unprocessedSegments)
        log.d("Generated ${newPolylines.size} polylines for unprocessed segments")
        return newPolylines
    }

    // Generates polylines for the whole track
    private fun getWholePathPolylines(segments: List<Segment>): List<PolylineOptions> {
        if (segments.isEmpty()) return emptyList()
        segmentsTracker.markSegmentsAsProcessed(segments)
        log.d("Generated ${segments.size} polylines for the whole track")
        return polylineProcessor.generatePolylines(segments)
    }

    override fun onCleared() {
        super.onCleared()
        serviceObservationsJob?.cancel()
        serviceObservationsJob = null
    }
}
