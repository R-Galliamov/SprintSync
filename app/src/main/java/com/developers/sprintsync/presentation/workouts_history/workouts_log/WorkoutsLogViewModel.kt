package com.developers.sprintsync.presentation.workouts_history.workouts_log

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class WorkoutsLogViewModel
    @Inject
    constructor(
        private val repository: TrackPreviewRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(UiState.LOADING)
        val state =
            _state
                .asStateFlow()
                .onStart { fetchTracks() }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.LOADING)

        private fun fetchTracks() {
            viewModelScope.launch {
                Presenter.present(repository.tracksWithPreviewsFlow, _state)
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

        private object Presenter {
            suspend fun present(
                dataFlow: Flow<List<TrackWithPreview>>,
                uiStateFlow: MutableStateFlow<UiState>,
            ) {
                uiStateFlow.update { UiState.LOADING }
                try {
                    dataFlow.collect { tracks ->
                        when {
                            tracks.isEmpty() -> uiStateFlow.update { UiState.EMPTY }
                            else -> {
                                val workoutLogItem = WorkoutLogItem.create(tracks)
                                uiStateFlow.update { UiState.success(workoutLogItem) }
                            }
                        }
                    }
                } catch (e: Exception) {
                    logError(e)
                    uiStateFlow.update { UiState.error() }
                }
            }

            private fun logError(exception: Exception) {
                Log.e(TAG, "Error fetching tracks: ${exception.message}", exception)
            }
        }

        companion object {
            private const val TAG = "RunHistoryViewModel"
        }
    }
