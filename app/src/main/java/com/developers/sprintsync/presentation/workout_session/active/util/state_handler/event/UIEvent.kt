package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event

import com.developers.sprintsync.presentation.workout_session.active.util.metrics_formatter.UiMetrics
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

sealed class UIEvent {
    data class UpdateTrackingUi(
        val metrics: UiMetrics,
        val polylines: List<PolylineOptions>,
    ) : UIEvent()

    data class RequestSnapshot(
        val bounds: LatLngBounds,
    ) : UIEvent()

    data class NavigateToSummary(
        val trackId: Int,
    ) : UIEvent()

    data class ErrorAndClose(val message: String) : UIEvent()
}
