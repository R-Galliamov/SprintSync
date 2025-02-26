package com.developers.sprintsync.tracking_session.presentation.tracking.util.polyline

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject

class PolylineOptionsCreator
    @Inject
    constructor() {
        companion object {
            private const val POLYLINE_COLOR = Color.RED // TODO fix color providing
            private const val POLYLINE_WIDTH = 7.5f
        }

        fun create(polylines: List<List<LatLng>>): List<PolylineOptions> =
            polylines.map { segment ->
                PolylineOptions().color(POLYLINE_COLOR).width(POLYLINE_WIDTH).addAll(segment)
            }
    }
