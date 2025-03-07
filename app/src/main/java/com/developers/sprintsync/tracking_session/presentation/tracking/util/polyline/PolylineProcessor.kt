package com.developers.sprintsync.tracking_session.presentation.tracking.util.polyline

import com.developers.sprintsync.core.components.track.data.model.Segments
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject

/**
 * Generates PolylineOptions for map rendering by formatting segments into polylines and styling them.
 * Uses injected PolylineFormatter and PolylineOptionsCreator.
 */
class PolylineProcessor
    @Inject
    constructor(
        private val polylineOptionsCreator: PolylineOptionsCreator,
        private val polylineFormatter: PolylineFormatter,
    ) {
        /**
         * Converts segments into a list of styled PolylineOptions for map rendering.
         *
         * @param segments The list of segments to process.
         * @return A list of PolylineOptions.
         */
        fun generatePolylines(segments: Segments): List<PolylineOptions> {
            val polylines = polylineFormatter.format(segments)
            return polylineOptionsCreator.create(polylines)
        }
    }
