package com.developers.sprintsync.run_history.presentation.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewWrapper
import com.developers.sprintsync.core.components.track_preview.domain.use_case.GetTrackPreviewWrapperUseCase
import com.developers.sprintsync.run_history.presentation.ui_model.UiTrackPreviewWrapper
import com.developers.sprintsync.run_history.presentation.ui_model.UiTrackPreviewWrapperFormatter
import com.developers.sprintsync.run_history.presentation.view_model.TrackHistoryViewModel.UiState
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
class TrackHistoryViewModel
    @Inject
    constructor(
        private val getTrackPreviewWrapperUseCase: GetTrackPreviewWrapperUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(UiState.LOADING)
        val state =
            _state
                .asStateFlow()
                .onStart { fetchTracks() }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.LOADING)

        private fun fetchTracks() {
            viewModelScope.launch {
                Presenter.present(getTrackPreviewWrapperUseCase(), _state)
            }
        }

        data class UiState(
            val showError: Boolean = false,
            val showLoadingOverlay: Boolean = false,
            val showEmptyTracksPlaceHolder: Boolean = false,
            val tracks: List<UiTrackPreviewWrapper> = emptyList(),
        ) {
            companion object {
                fun success(tracks: List<UiTrackPreviewWrapper>): UiState = UiState(tracks = tracks)

                fun error(): UiState = UiState(showError = true)

                val LOADING: UiState = UiState(showLoadingOverlay = true)
                val EMPTY: UiState = UiState(showEmptyTracksPlaceHolder = true)
            }
        }

        private object Presenter {
            suspend fun present(
                dataFlow: Flow<List<TrackPreviewWrapper>>,
                uiStateFlow: MutableStateFlow<UiState>,
            ) {
                uiStateFlow.update { UiState.LOADING }
                try {
                    dataFlow.collect { tracks ->
                        when {
                            tracks.isEmpty() -> uiStateFlow.update { UiState.EMPTY }
                            else -> {
                                val uiTracks = formatTracks(tracks)
                                uiStateFlow.update { UiState.success(uiTracks) }
                            }
                        }
                    }
                } catch (e: Exception) {
                    logError(e)
                    uiStateFlow.update { UiState.error() }
                }
            }

            private fun formatTracks(tracks: List<TrackPreviewWrapper>): List<UiTrackPreviewWrapper> =
                UiTrackPreviewWrapperFormatter.format(tracks)

            private fun logError(exception: Exception) {
                Log.e(TAG, "Error fetching tracks: ${exception.message}", exception)
            }
        }

        companion object {
            private const val TAG = "RunHistoryViewModel"
        }
    }
