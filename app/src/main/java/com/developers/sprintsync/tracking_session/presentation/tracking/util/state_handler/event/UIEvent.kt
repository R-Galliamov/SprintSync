package com.developers.sprintsync.tracking_session.presentation.tracking.util.state_handler.event

import com.developers.sprintsync.tracking_session.presentation.tracking.util.metrics_formatter.UiMetrics
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

sealed class UIEvent {
    data class UpdateTrackingUi(
        val metrics: UiMetrics,
        val polylines: PolylineOptions,
    ) : UIEvent()

    data class RequestSnapshot(
        val bounds: LatLngBounds,
    ) : UIEvent()

    data class NavigateToSummary(
        val trackId: Int,
    ) : UIEvent()

    data object ErrorAndClose : UIEvent()
}
