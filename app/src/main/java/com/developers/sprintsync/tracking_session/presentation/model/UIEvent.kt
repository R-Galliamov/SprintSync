package com.developers.sprintsync.tracking_session.presentation.model

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

    data object NavigateToSummary : UIEvent()

    data object ErrorAndClose : UIEvent()
}
