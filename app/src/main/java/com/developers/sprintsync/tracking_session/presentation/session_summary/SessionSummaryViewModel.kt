package com.developers.sprintsync.tracking_session.presentation.session_summary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.components.track.domain.use_case.DeleteTrackByIdUseCase
import com.developers.sprintsync.core.components.track.domain.use_case.GetTrackByIdUseCase
import com.developers.sprintsync.core.components.track.presentation.model.UiTrack
import com.developers.sprintsync.core.components.track.presentation.util.UiTrackFormatter
import com.developers.sprintsync.core.presentation.view.pace_chart.SegmentsToPaceChartMapper
import com.developers.sprintsync.core.presentation.view.pace_chart.model.PaceChartData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionSummaryViewModel
    @Inject
    constructor(
        private val getTrackByIdUseCase: GetTrackByIdUseCase,
        private val deleteTrackByIdUseCase: DeleteTrackByIdUseCase,
        private val segmentsToPaceChartMapper: SegmentsToPaceChartMapper,
        private val uiTrackFormatter: UiTrackFormatter,
    ) : ViewModel() {
        private val _state: MutableStateFlow<DataState> = MutableStateFlow(DataState.Loading)
        val state get() = _state.asStateFlow()

        fun fetchSessionData(trackId: Int) {
            if (state.value !is DataState.Loading) return
            _state.update { DataState.Loading }
            viewModelScope.launch {
                try {
                    val track = getTrackByIdUseCase(trackId)
                    val uiTrack = uiTrackFormatter.format(track)
                    val chartData = segmentsToPaceChartMapper.map(track.segments)
                    _state.update { DataState.Success(uiTrack, chartData) }
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                    _state.update { DataState.Error }
                }
            }
        }

        fun deleteTrackById(id: Int) {
            viewModelScope.launch {
                try {
                    deleteTrackByIdUseCase(id)
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                }
            }
        }

        sealed class DataState {
            data object Loading : DataState()

            data class Success(
                val track: UiTrack,
                val paceChartData: PaceChartData,
            ) : DataState()

            data object Error : DataState()
        }

        companion object {
            private const val TAG = "My stack: SessionSummaryViewModel"
        }
    }
