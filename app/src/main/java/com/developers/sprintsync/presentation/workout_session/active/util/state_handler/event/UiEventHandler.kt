package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event

import com.developers.sprintsync.core.util.extension.toLatLngBounds
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.log.logger.TrackLogger
import com.developers.sprintsync.data.map.TrackPreviewStyle
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.model.TrackingData
import com.developers.sprintsync.domain.track.model.TrackingStatus
import com.developers.sprintsync.domain.track.use_case.validator.TrackValidator
import com.developers.sprintsync.presentation.workout_session.active.util.polyline.PolylineFormatter
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class UIEvent {

    data class RequestSnapshot(
        val bounds: LatLngBounds,
        val style: TrackPreviewStyle,
    ) : UIEvent()

    data class NavigateToSummary(
        val trackId: Int,
    ) : UIEvent()

    data class ErrorAndClose(val message: String) : UIEvent()
}

/**
 * Handles UI events for the workout session based on tracking state.
 */
@ViewModelScoped
class UiEventHandler @Inject constructor(
    private val polylineFormatter: PolylineFormatter,
    private val trackCompletionHandler: TrackCompletionHandler,
    private val errorMessageProvider: TrackingErrorMessageProvider,
    private val snapshotStyle: TrackPreviewStyle,
    private val trackLog: TrackLogger,
    private val log: AppLogger,
) {

    private val _uiEventChannel = Channel<UIEvent>()
    val uiEventFlow = _uiEventChannel.receiveAsFlow()

    /**
     * Processes the tracking state to emit UI events.
     * @param state The [TrackingData] containing the current tracking status and track.
     */
    suspend fun handleState(state: TrackingData) {
        try {
            when (state.status) {
                is TrackingStatus.Completed -> handleCompleteState(state.track)
                else -> log.d("State ${state.status} received")
            }
        } catch (e: Exception) {
            log.e("Error handling state: ${e.message}", e)
            emitError(e)
        }
    }


    /**
     * Emits an error event for the given exception.
     * @param e The [Exception] to handle.
     */
    fun onError(e: Exception) = emitError(e)

    // Processes a completed track, saving it and requesting a snapshot
    private suspend fun handleCompleteState(track: Track) {
        trackLog.log(track)
        try {
            val trackId: Int =
                withContext(NonCancellable) {
                    TrackValidator.validateOrThrow(track) // pre-validation for optimizing resources
                    emitRequestSnapshot(track.segments, snapshotStyle)
                    trackCompletionHandler.saveTrackWithSnapshot(track)
                }
            _uiEventChannel.trySend(UIEvent.NavigateToSummary(trackId))
            log.i("Track processed, navigating to summary: id=$trackId")
        } catch (e: Exception) {
            log.e("Error completing track: ${e.message}", e)
            emitError(e)
        }
    }

    // Emits a request to capture a map snapshot
    private fun emitRequestSnapshot(segments: List<Segment>, style: TrackPreviewStyle) {
        try {
            val bounds = getLatBounds(segments)
            val event = UIEvent.RequestSnapshot(bounds, style)
            _uiEventChannel.trySend(event)
            log.d("Requested snapshot for segments: count=${segments.size}")
        } catch (e: Exception) {
            log.e("Error requesting snapshot: ${e.message}", e)
        }
    }

    // Emits an error event with a UI message
    private fun emitError(e: Exception) {
        try {
            val message = errorMessageProvider.toUiMessage(e)
            _uiEventChannel.trySend(UIEvent.ErrorAndClose(message))
            log.e("Error event emitted: $message", e)
        } catch (e: Exception) {
            log.e("Error emitting error event: ${e.message}", e)
        }
    }

    // Converts segments to LatLng bounds for snapshot
    private fun getLatBounds(segments: List<Segment>) = polylineFormatter.format(segments).flatten().toLatLngBounds()
}
