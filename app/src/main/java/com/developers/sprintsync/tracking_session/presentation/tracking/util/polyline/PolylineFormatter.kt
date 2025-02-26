package com.developers.sprintsync.tracking_session.presentation.tracking.util.polyline

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Segments
import com.developers.sprintsync.tracking.data.model.LocationModel
import com.developers.sprintsync.tracking.data.model.toLatLng
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import javax.inject.Singleton

class PolylineFormatter @Inject constructor() {
    fun format(segments: Segments): List<List<LatLng>> {
        val polylines = mutableListOf<List<LatLng>>()
        val currentPolyline = mutableListOf<LatLng>()

        var lastProcessedEndLocation: LocationModel? = null

        segments.forEach { segment ->
            if (segment is Segment.Active) {
                if (segment.startLocation != lastProcessedEndLocation) {
                    finalizePolyline(currentPolyline, polylines)
                }
                handleActiveSegment(segment, currentPolyline)
                lastProcessedEndLocation = segment.endLocation
            } else if (segment is Segment.Stationary) {
                finalizePolyline(currentPolyline, polylines)
            }
        }
        finalizePolyline(currentPolyline, polylines)

        return polylines
    }

    private fun handleActiveSegment(
        segment: Segment.Active,
        currentPolyline: MutableList<LatLng>,
    ) {
        if (currentPolyline.isEmpty()) {
            currentPolyline.add(segment.startLocation.toLatLng())
        }
        currentPolyline.add(segment.endLocation.toLatLng())
    }

    private fun finalizePolyline(
        currentPolyline: MutableList<LatLng>,
        polylines: MutableList<List<LatLng>>,
    ) {
        if (currentPolyline.isNotEmpty()) {
            polylines.add(currentPolyline.toList())
            currentPolyline.clear()
        }
    }
}
