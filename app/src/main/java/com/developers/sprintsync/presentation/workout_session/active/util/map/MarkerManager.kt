package com.developers.sprintsync.presentation.workout_session.active.util.map

import com.developers.sprintsync.core.util.log.AppLogger
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

/**
 * Manages a user location marker on a Google Map.
 */
@FragmentScoped
class MarkerManager @Inject constructor(
    private val log: AppLogger,
) {

    private var icon: BitmapDescriptor? = null

    private var _marker: Marker? = null
    val marker get() = _marker

    /**
     * Sets the icon for the marker.
     * @param icon The [BitmapDescriptor] to use for the marker.
     */
    fun setIcon(icon: BitmapDescriptor) {
        this.icon = icon
    }

    /**
     * Sets or updates the marker on the map at the specified position.
     * @param map The [GoogleMap] to place the marker on.
     * @param position The [LatLng] position for the marker.
     */
    fun updateMarker(
        map: GoogleMap,
        position: LatLng,
    ) {
        if (_marker == null) {
            _marker =
                map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .icon(icon),
                )
            log.i("Marker created at position: $position")
        } else if (marker?.position != position) {
            _marker?.position = position
            log.d("Marker updated to position: $position")
        }
    }
}
