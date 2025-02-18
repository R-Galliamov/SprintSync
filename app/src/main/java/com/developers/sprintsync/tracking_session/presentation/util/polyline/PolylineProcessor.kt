package com.developers.sprintsync.tracking_session.presentation.util.polyline

import com.developers.sprintsync.core.components.track.data.model.Segments
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject

class PolylineProcessor
    @Inject
    constructor(
        private val polylineTracker: PolylineTracker,
        private val polylineOptionsCreator: PolylineOptionsCreator,
    ) {
        fun generateNewPolylines(segments: Segments): PolylineOptions {
            val polylines: List<List<LatLng>> = polylineTracker.getNewPolylines(segments)
            return polylineOptionsCreator.createPolylineOptions(polylines)
        }
    }