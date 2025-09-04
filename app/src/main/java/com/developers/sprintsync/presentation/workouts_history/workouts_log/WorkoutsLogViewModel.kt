package com.developers.sprintsync.presentation.workouts_history.workouts_log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track_preview.model.TrackWithPreview
import com.developers.sprintsync.data.track_preview.repository.TrackPreviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the workout log UI state and data.
 */
@HiltViewModel
class WorkoutsLogViewModel
@Inject
constructor(
    private val repository: TrackPreviewRepository,
    private val logItemFormatter: WorkoutLogItemFormatter,
    private val log: AppLogger,
) : ViewModel() {

    private val presenter = Presenter()

    private val _state = MutableStateFlow(UiState.LOADING)
    val state =
        _state
            .asStateFlow()
            .onStart { fetchTracks() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.LOADING)

    /**
     * Initiates fetching of workout tracks.
     */
    private fun fetchTracks() {
        viewModelScope.launch {
            presenter.present(repository.tracksWithPreviewsFlow, _state)
        }
    }

    data class UiState(
        val showError: Boolean = false,
        val showLoadingOverlay: Boolean = false,
        val showEmptyTracksPlaceHolder: Boolean = false,
        val tracks: List<WorkoutLogItem> = emptyList(),
    ) {
        companion object {
            fun success(tracks: List<WorkoutLogItem>): UiState = UiState(tracks = tracks)
            fun error(): UiState = UiState(showError = true)
            val LOADING: UiState = UiState(showLoadingOverlay = true)
            val EMPTY: UiState = UiState(showEmptyTracksPlaceHolder = true)
        }
    }

    private inner class Presenter {
        suspend fun present(
            dataFlow: Flow<List<TrackWithPreview>>,
            uiStateFlow: MutableStateFlow<UiState>,
        ) {
            uiStateFlow.update { UiState.LOADING }
            try {
                dataFlow.collect { tws ->
                    when {
                        tws.isEmpty() -> {
                            uiStateFlow.update { UiState.EMPTY }
                            log.i("No tracks found, showing empty state")
                        }

                        else -> {
                            val workoutLogItems = logItemFormatter.format(tws)
                            uiStateFlow.update { UiState.success(workoutLogItems) }
                            log.i("Loaded ${workoutLogItems.size} workout log items")
                        }
                    }
                }
            } catch (e: Exception) {
                log.e("Error fetching tracks", e)
                uiStateFlow.update { UiState.error() }
            }
        }
    }
}
