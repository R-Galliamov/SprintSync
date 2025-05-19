package com.developers.sprintsync.presentation.workout_session.active.util.map

import android.content.Context
import com.developers.sprintsync.core.util.extension.adjustCameraToBounds
import com.developers.sprintsync.core.util.extension.setMapStyle
import com.developers.sprintsync.data.map.GoogleMapStyle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker

object MapSnapshotPreparer {
    fun prepareMap(
        context: Context,
        map: GoogleMap,
        bounds: LatLngBounds,
        padding: Int,
        marker: Marker? = null,
        mapStyle: GoogleMapStyle? = null,
    ) {
        marker?.remove()
        mapStyle?.let { map.setMapStyle(context, mapStyle) }
        map.adjustCameraToBounds(bounds, padding)
    }
}
