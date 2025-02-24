package com.developers.sprintsync.tracking_session.presentation.util.state_handler.map

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class MapStateHandler
    @Inject
    constructor() {
        private val _mapUiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
        val mapStateFlow get() = _mapUiState.asStateFlow()

        fun emitLocation(location: LatLng) {
            _mapUiState.update { MapUiState.Active(location) }
        }
    }