package com.developers.sprintsync.run_history.presentation.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewWrapper
import com.developers.sprintsync.core.components.track_preview.domain.use_case.GetTrackPreviewWrapperUseCase
import com.developers.sprintsync.run_history.presentation.ui_model.UiTrackPreviewWrapper
import com.developers.sprintsync.run_history.presentation.ui_model.UiTrackPreviewWrapperFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
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
        private val _state = MutableStateFlow<DataState>(DataState.Loading)
        val state =
            _state
                .asStateFlow()
                .onStart { fetchTracks() }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), DataState.Loading)

        private fun fetchTracks() {
            viewModelScope.launch {
                _state.update { DataState.Loading }
                handleTrackData()
            }
        }

        private suspend fun handleTrackData() {
            try {
                getTrackPreviewWrapperUseCase().collect { tracks ->
                    when {
                        tracks.isEmpty() -> _state.update { DataState.Empty }
                        else -> {
                            val uiTracks = formatTracks(tracks)
                            _state.update { DataState.Success(uiTracks) }
                        }
                    }
                }
            } catch (e: Exception) {
                logError(e)
                _state.update { DataState.Error(e) }
            }
        }

        private fun formatTracks(tracks: List<TrackPreviewWrapper>): List<UiTrackPreviewWrapper> =
            UiTrackPreviewWrapperFormatter.format(tracks)

        private fun logError(exception: Exception) {
            Log.e(TAG, "Error fetching tracks: ${exception.message}", exception)
        }

        sealed class DataState {
            data object Loading : DataState()

            data object Empty : DataState()

            data class Success(
                val tracks: List<UiTrackPreviewWrapper>,
            ) : DataState()

            data class Error(
                val exception: Exception,
            ) : DataState()
        }

        companion object {
            private const val TAG = "RunHistoryViewModel"
        }
    }
