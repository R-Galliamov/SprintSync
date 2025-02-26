package com.developers.sprintsync.tracking_session.presentation.tracking.util.polyline

import com.developers.sprintsync.core.components.track.data.model.Segments
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject

class PolylineProcessor
    @Inject
    constructor(
        private val polylineOptionsCreator: PolylineOptionsCreator,
        private val polylineFormatter: PolylineFormatter,
    ) {
        fun generateNewPolylines(segments: Segments): List<PolylineOptions> {
            val polylines = polylineFormatter.format(segments)
            val polylineOptions = polylineOptionsCreator.create(polylines)
            return polylineOptions
        }
    }
