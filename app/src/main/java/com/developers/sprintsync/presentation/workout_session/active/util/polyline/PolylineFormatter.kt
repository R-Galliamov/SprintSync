package com.developers.sprintsync.presentation.workout_session.active.util.polyline

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Segments
import com.developers.sprintsync.domain.tracking_service.model.LocationModel
import com.developers.sprintsync.domain.tracking_service.model.toLatLng
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

/**
 * A formatter that converts a list of segments into a list of polylines for map visualization.
 * Polylines are created by connecting the start and end locations of active segments, splitting into
 * separate polylines when there are gaps in location continuity or when a stationary segment is encountered.
 */
class PolylineFormatter @Inject constructor() {

    /**
     * Formats a list of segments into a list of polylines.
     *
     * This method processes each segment in the input list. For active segments, it builds a polyline by
     * connecting consecutive locations, splitting into a new polyline if the start location of an active
     * segment does not match the end location of the previous segment. Stationary segments cause a break
     * in the polyline, finalizing the current one.
     *
     * @param segments The list of segments to format.
     * @return A list of polylines, where each polyline is a list of LatLng coordinates.
     */
    fun format(segments: Segments): List<List<LatLng>> {
        // List to store all completed polylines
        val polylines = mutableListOf<List<LatLng>>()
        // Current polyline being built
        val currentPolyline = mutableListOf<LatLng>()

        // Tracks the end location of the last processed active segment to check for continuity
        var lastProcessedEndLocation: LocationModel? = null

        segments.forEach { segment ->
            when (segment) {
                is Segment.Active -> {
                    // If the start location doesn't match the last end location, start a new polyline
                    if (segment.startLocation != lastProcessedEndLocation) {
                        finalizePolyline(currentPolyline, polylines)
                    }
                    handleActiveSegment(segment, currentPolyline)
                    lastProcessedEndLocation = segment.endLocation
                }
                is Segment.Stationary -> {
                    // Stationary segment breaks the polyline continuity
                    finalizePolyline(currentPolyline, polylines)
                }
            }
        }
        // Finalize any remaining points in the current polyline
        finalizePolyline(currentPolyline, polylines)

        return polylines
    }

    /**
     * Adds the start and end locations of an active segment to the current polyline.
     *
     * If the polyline is empty, the start location is added first. The end location is always added.
     *
     * @param segment The active segment to process.
     * @param currentPolyline The current polyline being built.
     */
    private fun handleActiveSegment(
        segment: Segment.Active,
        currentPolyline: MutableList<LatLng>,
    ) {
        // Add the start location only if the polyline is empty
        if (currentPolyline.isEmpty()) {
            currentPolyline.add(segment.startLocation.toLatLng())
        }
        currentPolyline.add(segment.endLocation.toLatLng())
    }

    /**
     * Finalizes the current polyline by adding it to the list of polylines and clearing it.
     *
     * This method ensures that only non-empty polylines are added to the final list.
     *
     * @param currentPolyline The current polyline being built.
     * @param polylines The list of completed polylines.
     */
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