package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event

import com.google.android.gms.maps.model.LatLngBounds

sealed class UIEvent { // TODO move to handler

    data class RequestSnapshot(
        val bounds: LatLngBounds,
    ) : UIEvent()

    data class NavigateToSummary(
        val trackId: Int,
    ) : UIEvent()

    data class ErrorAndClose(val message: String) : UIEvent()
}
