package com.developers.sprintsync.presentation.workout_session.active.util.map

import android.content.Context
import com.developers.sprintsync.core.util.extension.adjustCameraToBounds
import com.developers.sprintsync.core.util.extension.setMapStyle
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.map.GoogleMapStyle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

/**
 * Prepares a Google Map for snapshot capture by adjusting camera, style, and markers.
 */
@FragmentScoped
class MapSnapshotPreparer @Inject constructor(private val log: AppLogger) {
    /**
     * Configures the map for a snapshot.
     * @param context The [Context] for accessing resources.
     * @param map The [GoogleMap] to prepare.
     * @param bounds The [LatLngBounds] to frame the camera.
     * @param padding The padding (in pixels) around the bounds.
     * @param marker Optional [Marker] to remove before capture.
     * @param mapStyle Optional [GoogleMapStyle] to apply.
     */
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
        log.i("Map prepared for snapshot: bounds=$bounds, padding=$padding")
    }
}
