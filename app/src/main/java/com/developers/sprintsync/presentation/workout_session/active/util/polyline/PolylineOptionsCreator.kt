package com.developers.sprintsync.presentation.workout_session.active.util.polyline

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject

/** Creates PolylineOptions for map rendering with consistent styling. */
class PolylineOptionsCreator
@Inject
constructor() {
    companion object {
        private const val POLYLINE_COLOR = Color.RED
        private const val POLYLINE_WIDTH = 7.5f
    }

    /**
     * Converts a list of polylines into styled PolylineOptions using predefined color and line width.
     *
     * @param polylines List of polylines, each a list of LatLng points.
     * @return List of PolylineOptions with default color and width.
     */
    fun create(polylines: List<List<LatLng>>): List<PolylineOptions> =
        polylines.map { segment ->
            PolylineOptions().color(POLYLINE_COLOR).width(POLYLINE_WIDTH).addAll(segment)
        }
}