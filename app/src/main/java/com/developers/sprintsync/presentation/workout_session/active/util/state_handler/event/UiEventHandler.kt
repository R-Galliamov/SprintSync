package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event

import com.developers.sprintsync.core.util.extension.toLatLngBounds
import com.developers.sprintsync.core.util.log.logger.TrackLogger
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.model.TrackingData
import com.developers.sprintsync.domain.track.model.TrackingStatus
import com.developers.sprintsync.domain.track.use_case.validator.TrackValidator
import com.developers.sprintsync.presentation.workout_session.active.util.metrics_formatter.UiMetrics
import com.developers.sprintsync.presentation.workout_session.active.util.polyline.PolylineFormatter
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class UiEventHandler @Inject constructor(
    private val polylineFormatter: PolylineFormatter,
    private val trackCompletionHandler: TrackCompletionHandler,
    private val errorMessageProvider: TrackingErrorMessageProvider,
) {

    private val _uiEventChannel = Channel<UIEvent>()
    val uiEventFlow = _uiEventChannel.receiveAsFlow()

    suspend fun handleState(state: TrackingData) {
        when (state.status) {
            TrackingStatus.COMPLETED -> handleCompleteState(state.track)
            else -> {
                // NO-OP
            }
        }
    }

    fun onError(e: Exception) = emitError(e)

    private suspend fun handleCompleteState(track: Track) {
        TrackLogger.log(track)
        try {
            val trackId: Int =
                withContext(NonCancellable) {
                    TrackValidator.validateOrThrow(track) // pre-validation for optimizing resources
                    val event = UIEvent.RequestSnapshot(getLatLngBounds(track.segments))
                    _uiEventChannel.trySend(event)
                    trackCompletionHandler.saveTrackWithSnapshot(track)
                }
            _uiEventChannel.trySend(UIEvent.NavigateToSummary(trackId))
        } catch (e: Exception) {
            emitError(e)
        }
    }

    private fun emitError(e: Exception) {
        val message = errorMessageProvider.toUiMessage(e)
        _uiEventChannel.trySend(UIEvent.ErrorAndClose(message))
    }

    private fun getLatLngBounds(segments: List<Segment>) = polylineFormatter.format(segments).flatten().toLatLngBounds()

}
