package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event

import com.developers.sprintsync.core.util.extension.toLatLngBounds
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.log.logger.TrackLogger
import com.developers.sprintsync.data.map.TrackPreviewStyle
import com.developers.sprintsync.domain.core.AppResult
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.model.TrackingData
import com.developers.sprintsync.domain.track.model.TrackingStatus
import com.developers.sprintsync.domain.track.validator.TrackErrors
import com.developers.sprintsync.presentation.components.UIError
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

    data class ErrorAndClose(val err: Set<UIError>) : UIEvent()
}

/**
 * Handles UI events for the workout session based on tracking state.
 */
@ViewModelScoped
class UiEventHandler @Inject constructor(
    private val polylineFormatter: PolylineFormatter,
    private val trackCompletionHandler: TrackCompletionHandler,
    private val snapshotStyle: TrackPreviewStyle,
    private val trackLog: TrackLogger,
    private val log: AppLogger,
) {

    private val _uiEventChannel = Channel<UIEvent>()
    val uiEventFlow = _uiEventChannel.receiveAsFlow()

    suspend fun handleState(state: TrackingData) {
        try {
            when (state.status) {
                is TrackingStatus.Completed -> handleCompleteState(state.track)
                else -> log.d("State ${state.status} received")
            }
        } catch (e: Exception) {
            log.e("Error handling state: ${e.message}", e)
            emitError(e.toUiErr())
        }
    }

    fun onError(e: Exception) = emitError(e.toUiErr())

    private suspend fun handleCompleteState(track: Track) {
        trackLog.log(track)

        emitRequestSnapshot(track.segments, snapshotStyle)

        val trackId = withContext(NonCancellable) {
            when (val res = trackCompletionHandler.saveTrackWithSnapshot(track)) {
                is AppResult.Success -> res.value
                is AppResult.Failure.Validation -> {
                    emitErrors(res.errors.toUiErr())
                    log.w("Validation failed, closing with errors: ${res.errors}")
                    null
                }

                is AppResult.Failure.Unexpected -> {
                    emitError(res.cause.toUiErr())
                    log.e("Unexpected error saving track: ${res.cause.message}", res.cause)
                    null
                }
            }
        } ?: return

        _uiEventChannel.trySend(UIEvent.NavigateToSummary(trackId))
        log.i("Track processed, navigating to summary: id=$trackId")
    }

    // Emits a request to capture a map snapshot
    private fun emitRequestSnapshot(segments: List<Segment>, style: TrackPreviewStyle) {
        runCatching { getLatBounds(segments) }
            .onSuccess { bounds ->
                if (bounds != null) {
                    _uiEventChannel.trySend(UIEvent.RequestSnapshot(bounds, style))
                    log.d("Requested snapshot. segments=${segments.size}")
                } else {
                    log.d("No bounds for snapshot. segments=${segments.size}")
                }
            }
            .onFailure { e -> log.e("Error requesting snapshot: ${e.message}", e) }
    }

    private fun emitError(error: UIError) {
        emitErrors(setOf(error))
    }


    private fun emitErrors(errors: Set<UIError>) {
        _uiEventChannel.trySend(UIEvent.ErrorAndClose(errors))
    }

    private fun getLatBounds(segments: List<Segment>): LatLngBounds? {
        val points = polylineFormatter.format(segments).flatten()
        return if (points.isNotEmpty()) points.toLatLngBounds() else null
    }
}

private fun Set<TrackErrors>.toUiErr(): Set<UIError> = map { err ->
    when (err) {
        TrackErrors.InvalidTimestamp -> UIError.INVALID_TIMESTAMP
        TrackErrors.AvgPaceInvalid -> UIError.AVG_PACE_INVALID
        TrackErrors.BestPaceInvalid -> UIError.BEST_PACE_INVALID
        TrackErrors.CaloriesNegative -> UIError.CALORIES_NEGATIVE
        TrackErrors.DurationTooShort -> UIError.DURATION_TOO_SHORT
        TrackErrors.DistanceTooShort -> UIError.DISTANCE_TOO_SHORT
        TrackErrors.TooFewSegments -> UIError.TOO_FEW_SEGMENTS
    }
}.toSet()

private fun Throwable.toUiErr() = UIError.UNSPECIFIED
