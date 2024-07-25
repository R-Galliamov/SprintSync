package com.developers.sprintsync.tracking.analytics.ui.map.util

import com.developers.sprintsync.tracking.session.model.track.Segment
import com.developers.sprintsync.tracking.session.model.track.Segments
import com.developers.sprintsync.tracking.session.model.track.toLatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlin.math.max

class MapCalculations {
    companion object {
        fun calculateBounds(segments: Segments): LatLngBounds {
            val bounds = LatLngBounds.Builder()
            for (segment in segments) {
                if (segment is Segment.ActiveSegment) {
                    bounds.include(segment.startLocation.toLatLng())
                    bounds.include(segment.endLocation.toLatLng())
                }
            }
            return bounds.build()
        }

        fun calculateTrackPadding(
            mapWidth: Int,
            mapHeight: Int,
        ): Int {
            val horizontalPadding = (mapWidth * TRACK_PADDING_PERCENTAGE).toInt()
            val verticalPadding = (mapHeight * TRACK_PADDING_PERCENTAGE).toInt()
            return max(horizontalPadding, verticalPadding)
        }

        const val TRACK_PADDING_PERCENTAGE = 0.05f
    }
}
