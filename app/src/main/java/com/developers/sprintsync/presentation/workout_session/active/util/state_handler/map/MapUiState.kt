package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.map

import com.google.android.gms.maps.model.LatLng

sealed class MapUiState {
    data object Loading : MapUiState()

    data class Active(
        val location: LatLng,
    ) : MapUiState()
}