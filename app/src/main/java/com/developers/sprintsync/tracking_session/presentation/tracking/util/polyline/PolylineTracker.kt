package com.developers.sprintsync.tracking_session.presentation.tracking.util.polyline

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Segments
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class PolylineTracker
    @Inject
    constructor() {
        private val processedSegments = mutableListOf<Segment>()

        fun getNewPolylines(segments: Segments): List<List<LatLng>> {
            val newSegments = segments.filter { segment -> segment !in processedSegments }
            processedSegments.addAll(newSegments)
            return PolylineFormatter.format(newSegments)
        }
    }