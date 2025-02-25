package com.developers.sprintsync.tracking_session.presentation.tracking.util.map

import android.content.Context
import com.developers.sprintsync.core.util.extension.adjustCameraToBounds
import com.developers.sprintsync.core.util.extension.setMapStyle
import com.developers.sprintsync.map.data.model.MapStyle
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
        mapStyle: MapStyle? = null,
    ) {
        marker?.remove()
        mapStyle?.let { map.setMapStyle(context, mapStyle) }
        map.adjustCameraToBounds(bounds, padding)
    }
}
