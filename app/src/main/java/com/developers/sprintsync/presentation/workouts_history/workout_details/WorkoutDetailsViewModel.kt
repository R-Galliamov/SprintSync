package com.developers.sprintsync.presentation.workouts_history.workout_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.util.view.pace_chart.SegmentsToPaceChartMapper
import com.developers.sprintsync.core.util.view.pace_chart.model.PaceChartData
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.track.use_case.storage.DeleteTrackByIdUseCase
import com.developers.sprintsync.domain.track.use_case.storage.GetTrackByIdUseCase
import com.developers.sprintsync.presentation.components.TrackDisplayModel
import com.developers.sprintsync.presentation.components.TrackDisplayModelCreator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing workout details data and state.
 */
@HiltViewModel
class WorkoutDetailsViewModel
@Inject
constructor(
    private val getTrackByIdUseCase: GetTrackByIdUseCase,
    private val deleteTrackByIdUseCase: DeleteTrackByIdUseCase,
    private val segmentsToPaceChartMapper: SegmentsToPaceChartMapper,
    private val trackDisplayModelCreator: TrackDisplayModelCreator,
    private val log: AppLogger,
) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    /**
     * Fetches track data for the specified track ID.
     * @param trackId The ID of the track to fetch.
     */
    fun fetchTrackData(trackId: Int) {
        if (state.value !is State.Loading) return
        _state.update { State.Loading }
        viewModelScope.launch {
            try {
                val track = getTrackByIdUseCase(trackId)
                val uiTrack = trackDisplayModelCreator.create(track)
                val chartData = segmentsToPaceChartMapper.map(track.segments)
                _state.update { State.Success(uiTrack, chartData) }
                log.i("Fetched track data for trackId=$trackId")
            } catch (e: Exception) {
                log.e("Failed to fetch track data for trackId=$trackId", e)
                _state.update { State.Error(e) }
            }
        }
    }

    /**
     * Deletes the track with the specified ID.
     * @param trackId The ID of the track to delete.
     */
    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            try {
                deleteTrackByIdUseCase(trackId)
                log.i("Deleted track: trackId=$trackId")
            } catch (e: Exception) {
                log.e("Failed to delete track: trackId=$trackId", e)
            }
        }
    }

    sealed class State {
        data object Loading : State()

        data class Success(
            val track: TrackDisplayModel,
            val paceChartData: PaceChartData,
        ) : State()

        data class Error(
            val e: Exception,
        ) : State()
    }
}
