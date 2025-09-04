package com.developers.sprintsync.presentation.map_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.util.extension.toLatLngBounds
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.toLatLng
import com.developers.sprintsync.domain.track.use_case.storage.GetTrackByIdUseCase
import com.developers.sprintsync.presentation.workout_session.active.util.polyline.PolylineProcessor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel
@Inject
constructor(
    private val getTrackByIdUseCase: GetTrackByIdUseCase,
    private val polylineProcessor: PolylineProcessor,
    private val log: AppLogger,
) : ViewModel() {


    sealed class UiState {
        data object Loading : UiState()
        data class Success(val polylines: List<PolylineOptions>, val bounds: LatLngBounds, val padding: Int = 40) :
            UiState()
    }

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val state: StateFlow<UiState> get() = _state.asStateFlow()

    fun fetchTrack(trackId: Int) {
        viewModelScope.launch {
            val track = getTrackByIdUseCase(trackId)
            if (track.segments.isEmpty()) return@launch
            val polylines = polylineProcessor.generatePolylines(track.segments)
            val bounds = track.segments.toLatLngs().toLatLngBounds()
            val next = UiState.Success(polylines, bounds)
            log.d("Polylines generated: ${polylines.size}")
            log.d("Bounds generated: $bounds")
            _state.update { next }
        }
    }

}


fun List<Segment>.toLatLngs(): List<LatLng> =
    flatMap { listOf(it.startLocation.toLatLng(), it.endLocation.toLatLng()) }