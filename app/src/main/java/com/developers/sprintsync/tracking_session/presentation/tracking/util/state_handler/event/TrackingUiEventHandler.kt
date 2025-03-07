package com.developers.sprintsync.tracking_session.presentation.tracking.util.state_handler.event

import android.util.Log
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.domain.use_case.ValidateTrackUseCase
import com.developers.sprintsync.core.util.extension.toLatLngBounds
import com.developers.sprintsync.core.util.logger.TrackLogger
import com.developers.sprintsync.core.util.validation.ValidationException
import com.developers.sprintsync.tracking.component.model.TrackState
import com.developers.sprintsync.tracking.component.model.TrackingStatus
import com.developers.sprintsync.tracking.component.use_case.ResetCurrentTrackingStateUseCase
import com.developers.sprintsync.tracking_session.presentation.tracking.util.metrics_formatter.UiMetricsFormatter
import com.developers.sprintsync.tracking_session.presentation.tracking.util.polyline.PolylineFormatter
import com.developers.sprintsync.tracking_session.presentation.tracking.util.polyline.PolylineProcessor
import com.developers.sprintsync.tracking_session.presentation.tracking.util.segments.SegmentsTracker
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class TrackingUiEventHandler
    @Inject
    constructor(
        private val validateTrackOrThrowUseCase: ValidateTrackUseCase,
        private val segmentsTracker: SegmentsTracker,
        private val polylineProcessor: PolylineProcessor,
        private val polylineFormatter: PolylineFormatter,
        private val trackCompletionHandler: TrackCompletionHandler,
        private val resetCurrentTrackingStateUseCase: ResetCurrentTrackingStateUseCase,
    ) {
        private val _uiEventFlow = MutableStateFlow<UIEvent?>(null)
        val uiEventFlow get() = _uiEventFlow.asStateFlow().filterNotNull()

        suspend fun handleState(state: TrackState) {
            when (state.status) {
                TrackingStatus.COMPLETED -> handleCompleteState(state.track)
                else -> emitUiTrackData(state.track)
            }
        }

        private suspend fun handleCompleteState(track: Track) {
            TrackLogger.log(track)
            try {
                val trackId: Int =
                    withContext(NonCancellable) {
                        validateTrackOrThrowUseCase(track)
                        val bounds = polylineFormatter.format(track.segments).flatten().toLatLngBounds()
                        _uiEventFlow.update { UIEvent.RequestSnapshot(bounds) }
                        trackCompletionHandler.saveTrackWithSnapshot(track)
                    }
                _uiEventFlow.update { UIEvent.NavigateToSummary(trackId) }
            } catch (e: Exception) {
                if (e is ValidationException) {
                    Log.e(TAG, e.message.toString(), e)
                } else {
                    Log.e(TAG, "Unexpected error while handling track completion", e)
                }
                _uiEventFlow.update { UIEvent.ErrorAndClose }
            } finally {
                resetCurrentTrackingStateUseCase()
            }
        }

        private fun emitUiTrackData(track: Track) {
            val metrics = UiMetricsFormatter.format(track)
            val segments = segmentsTracker.getNewSegmentsAndAdd(track.segments)
            val polylines = polylineProcessor.generatePolylines(segments)
            _uiEventFlow.update { UIEvent.UpdateTrackingUi(metrics, polylines) }
        }

        companion object {
            private const val TAG = "My stack: TrackingUiEventHandler"
        }
    }
