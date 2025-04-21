package com.developers.sprintsync.presentation.workout_session.active.util.polyline

import com.developers.sprintsync.domain.track.model.Segment
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
        fun generatePolylines(segments: List<Segment>): List<PolylineOptions> {
            val polylines = polylineFormatter.format(segments)
            return polylineOptionsCreator.create(polylines)
        }
    }
