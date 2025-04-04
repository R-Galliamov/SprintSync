package com.developers.sprintsync.presentation.workout_session.active.util.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MarkerManager(
    private val icon: BitmapDescriptor?,
) {
    private var _marker: Marker? = null
    val marker get() = _marker

    fun setMarker(
        map: GoogleMap,
        position: LatLng,
    ) {
        if (_marker == null) {
            _marker =
                map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(TITLE)
                        .icon(icon),
                )
        } else if (marker?.position != position) {
            _marker?.position = position
        }
    }

    private companion object {
        private const val TITLE = "User Location"
    }
}
