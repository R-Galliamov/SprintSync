package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.ui

import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.model.TrackingData
import com.developers.sprintsync.domain.track.model.TrackingStatus
import com.developers.sprintsync.presentation.workout_session.active.util.metrics_formatter.UiMetrics
import com.developers.sprintsync.presentation.workout_session.active.util.metrics_formatter.UiMetricsFormatter
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class UIState {

    data object Loading : UIState()

    data object Initialized : UIState()

    data object Active : UIState()

    data object Paused : UIState()

    data object Completing : UIState()
}

@ViewModelScoped
class UiStateHandler @Inject constructor() {
    private val _uiStateFlow = MutableStateFlow<UIState>(UIState.Loading)
    val uiStateFlow: StateFlow<UIState> get() = _uiStateFlow.asStateFlow()

    private val _metricsFlow = MutableStateFlow(UiMetrics.INITIAL)
    val metricsFlow: StateFlow<UiMetrics> get() = _metricsFlow.asStateFlow()

    private val _durationFlow = MutableStateFlow(formatDuration(0))
    val durationFlow: StateFlow<String> = _durationFlow.asStateFlow()

    fun handleStatus(status: TrackingStatus) {
        val newState =
            when (status) {
                TrackingStatus.INITIALIZED -> UIState.Initialized
                TrackingStatus.ACTIVE -> UIState.Active
                TrackingStatus.PAUSED -> UIState.Paused
                TrackingStatus.COMPLETED -> UIState.Completing
            }

        if (newState != _uiStateFlow.value) {
            _uiStateFlow.update { newState }
        }
    }

    fun handleTrack(track: Track) {
        val metrics = getMetrics(track)
        _metricsFlow.update { metrics }
    }

    fun handleDuration(duration: Long) {
        val durationString = formatDuration(duration)
        _durationFlow.update { (durationString) }
    }

    private fun formatDuration(duration: Long) = DurationUiFormatter.format(duration, DurationUiPattern.HH_MM_SS)

    private fun getMetrics(track: Track): UiMetrics {
        val distance = track.distanceMeters
        val calories = track.calories
        val segment = track.segments.lastOrNull()
        return UiMetrics.create(distance, calories, segment)
    }
}