package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.ui

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.model.TrackingStatus
import com.developers.sprintsync.presentation.workout_session.active.util.metrics_formatter.UiMetrics
import com.developers.sprintsync.presentation.workout_session.active.util.view.TrackingController
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Represents the UI state of the workout session.
 */
sealed class UIState {
    val trackingControllerState: TrackingController.State? get() = getTrackingPanelState()
    val showInitialLoading: Boolean get() = this is Loading
    val showCompletingLoading: Boolean get() = this is Completing
    val shouldStopLocationUpdatesWhenClosed: Boolean get() = this !is Active

    data object Loading : UIState()

    data object Initialized : UIState()

    data object Active : UIState()

    data object Paused : UIState()

    data object Completing : UIState()

    private fun getTrackingPanelState(): TrackingController.State? = when (this) {
        is Loading, Completing -> null
        is Initialized -> TrackingController.State.Initialized
        is Active -> TrackingController.State.Active
        is Paused -> TrackingController.State.Paused
    }
}

/**
 * Manages the UI state and metrics for the workout session.
 */
@ViewModelScoped
class UiStateHandler @Inject constructor(private val log: AppLogger) {
    private val _uiStateFlow = MutableStateFlow<UIState>(UIState.Loading)
    val uiStateFlow: StateFlow<UIState> get() = _uiStateFlow.asStateFlow()

    private val _metricsFlow = MutableStateFlow(UiMetrics.INITIAL)
    val metricsFlow: StateFlow<UiMetrics> get() = _metricsFlow.asStateFlow()

    private val _durationFlow = MutableStateFlow(formatDuration(0))
    val durationFlow: StateFlow<String> = _durationFlow.asStateFlow()

    /**
     * Updates the UI state based on tracking status.
     * @param status The [TrackingStatus] to handle.
     */
    fun handleStatus(status: TrackingStatus) {
        try {
            val newState = when (status) {
                TrackingStatus.INITIALIZED -> UIState.Initialized
                TrackingStatus.ACTIVE -> UIState.Active
                TrackingStatus.PAUSED -> UIState.Paused
                TrackingStatus.COMPLETED -> UIState.Completing
            }

            if (newState != _uiStateFlow.value) {
                _uiStateFlow.update { newState }
                log.d("UI state updated to: $newState")
            }
        } catch (e: Exception) {
            log.e("Error handling status $status: ${e.message}", e)
        }
    }

    /**
     * Updates metrics based on the track data.
     * @param track The [Track] to process.
     */
    fun handleTrack(track: Track) {
        try {
            val metrics = getMetrics(track)
            _metricsFlow.update { metrics }
            log.d("Metrics updated for track: distance=${track.distanceMeters}, segments=${track.segments.size}")
        } catch (e: Exception) {
            log.e("Error handling track: ${e.message}", e)
        }
    }

    /**
     * Updates the duration display.
     * @param duration The duration in milliseconds.
     */
    fun handleDuration(duration: Long) {
        try {
            val durationString = formatDuration(duration)
            _durationFlow.update { durationString }
        } catch (e: Exception) {
            log.e("Error handling duration: ${e.message}", e)
        }
    }

    // Formats duration for UI display
    private fun formatDuration(duration: Long) = DurationUiFormatter.format(duration, DurationUiPattern.HH_MM_SS)

    // Creates UI metrics from track data
    private fun getMetrics(track: Track): UiMetrics {
        val distance = track.distanceMeters
        val calories = track.calories
        val segment = track.segments.lastOrNull()
        return UiMetrics.create(distance, calories, segment)
    }
}