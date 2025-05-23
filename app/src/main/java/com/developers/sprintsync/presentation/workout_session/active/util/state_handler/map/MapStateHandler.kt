package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.map

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class MapUiState {
    data object Loading : MapUiState()

    data class LocationUpdated(val location: LatLng) : MapUiState()

    data class PolylinesUpdated(val polylines: List<PolylineOptions>) : MapUiState()
}

@ViewModelScoped
class MapStateHandler @Inject constructor() {
    private val _mapUiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val mapStateFlow get() = _mapUiState.asStateFlow()

    fun emitLocation(location: LatLng) {
        _mapUiState.update { MapUiState.LocationUpdated(location) }
    }

    fun emitPolylines(polylines: List<PolylineOptions>) {
        _mapUiState.update { MapUiState.PolylinesUpdated(polylines) }
    }
}
