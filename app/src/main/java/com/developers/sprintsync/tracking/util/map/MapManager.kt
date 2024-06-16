package com.developers.sprintsync.tracking.util.map

import android.content.Context
import com.developers.sprintsync.R
import com.developers.sprintsync.global.manager.AppThemeManager
import com.developers.sprintsync.global.util.extension.getBitmapDescriptor
import com.developers.sprintsync.tracking.model.Segment
import com.developers.sprintsync.tracking.model.Segments
import com.developers.sprintsync.tracking.model.toLatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapManager(
    private val context: Context,
) {
    private var _map: GoogleMap? = null

    private val map get() = checkNotNull(_map) { "Map is null" }

    private var currentUserMarker: Marker? = null

    private val themeManager = AppThemeManager(context)

    private val polylineColor = themeManager.getFourthlyColor()

    private val polylineWidth = 7.5f

    fun initialize(map: GoogleMap) {
        this@MapManager._map = map
    }

    fun addPolyline(segment: Segment) {
        if (segment is Segment.ActiveSegment) {
            val startLocation = segment.startLocation.toLatLng()
            val endLocation = segment.endLocation.toLatLng()
            addPolyline(startLocation, endLocation)
        }
    }

    fun addPolylines(segments: Segments) {
        segments.forEach { segment ->
            if (segment is Segment.ActiveSegment) {
                val startLocation = segment.startLocation.toLatLng()
                val endLocation = segment.endLocation.toLatLng()
                addPolyline(startLocation, endLocation)
            }
        }
    }

    fun moveCameraToLocation(latLng: LatLng) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                15.0f,
            ),
        )
    }

    // TODO: add start point
    fun moveCameraToSegments(segments: Segments) {
        val bounds = getBounds(segments)
        adjustCameraToBounds(bounds)
    }

    fun updateUserMarker(latLng: LatLng) {
        if (currentUserMarker == null) {
            val icon = context.getBitmapDescriptor(R.drawable.ic_user_location)
            currentUserMarker =
                map.addMarker(
                    MarkerOptions().position(latLng).title("Current Location")
                        .icon(icon),
                )
        } else {
            currentUserMarker?.position = latLng
        }
    }

    private fun addPolyline(
        startLatLng: LatLng,
        endLatLng: LatLng,
    ) {
        val polylineOptions =
            PolylineOptions().color(polylineColor).width(polylineWidth).add(startLatLng, endLatLng)
        map.addPolyline(polylineOptions)
    }

    // TODO: add width and height
    private fun getBounds(segments: Segments): LatLngBounds {
        val bounds = LatLngBounds.Builder()
        for (segment in segments) {
            if (segment is Segment.ActiveSegment) {
                bounds.include(segment.endLocation.toLatLng())
            }
        }
        return bounds.build()
    }

    private fun adjustCameraToBounds(bounds: LatLngBounds) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, 100),
        )
    }
}
