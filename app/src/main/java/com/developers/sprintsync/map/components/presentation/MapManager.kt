package com.developers.sprintsync.map.components.presentation

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.style_provider.AppThemeProvider
import com.developers.sprintsync.core.util.extension.getBitmapDescriptor
import com.developers.sprintsync.core.util.interfaces.Clearable
import com.developers.sprintsync.core.util.bitmap.BitmapCropper
import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Segments
import com.developers.sprintsync.core.tracking_service.data.model.location.toLatLng
import com.developers.sprintsync.map.presentation.util.calculator.MapCalculations
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapManager(
    private val context: Context,
) : Clearable {
    private var _map: GoogleMap? = null

    private val map get() = checkNotNull(_map) { "Map is null" }

    private var currentUserMarker: Marker? = null

    private val colors = AppThemeProvider(context).Color()

    private val polylineColor = colors.fourthly

    fun initialize(map: GoogleMap) {
        this@MapManager._map = map
    }

    fun setMapStyle(mapStyleOptions: MapStyleOptions) {
        val success = map.setMapStyle(mapStyleOptions)
        Log.d(TAG, "setMapStyle: $success")
        if (!success) {
            Log.e(TAG, "Style parsing failed.")
        }
    }

    fun addPolyline(segment: Segment) {
        if (segment is Segment.ActiveSegment) {
            val startLocation = segment.startLocation.toLatLng()
            val endLocation = segment.endLocation.toLatLng()
            addPolyline(startLocation, endLocation)
            Log.d(TAG, "addPolyline")
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

    fun moveCameraToLocation(
        latLng: LatLng,
        animate: Boolean = true,
    ) {
        if (animate) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    ZOOM_LEVEL,
                ),
            )
        } else {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL)
            map.moveCamera(cameraUpdate)
        }
    }

    fun adjustCameraToSegments(
        segments: Segments,
        mapWidth: Int,
        mapHeight: Int,
    ) {
        val bounds = MapCalculations.calculateBounds(segments)
        val padding = MapCalculations.calculateTrackPadding(mapWidth, mapHeight)
        adjustCameraToBounds(bounds, padding)
    }

    fun updateUserMarker(latLng: LatLng) {
        if (currentUserMarker == null) {
            val icon = context.getBitmapDescriptor(R.drawable.ic_user_location)
            currentUserMarker =
                map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Current Location")
                        .icon(icon),
                )
        } else {
            currentUserMarker?.position = latLng
        }
    }

    fun captureTrackSnapshot(
        segments: Segments,
        mapWidth: Int,
        mapHeight: Int,
        targetWidth: Int,
        targetHeight: Int,
        mapStyle: MapStyleOptions? = null,
        onSnapshotReady: (Bitmap?) -> Unit,
    ) {
        mapStyle?.let { setMapStyle(it) }
        adjustCameraToSegments(segments, mapWidth, mapHeight)
        currentUserMarker?.remove()
        map.setOnMapLoadedCallback {
            map.snapshot { bitmap ->
                if (bitmap != null) {
                    val resizedBitmap =
                        BitmapCropper.cropToTargetDimensions(bitmap, targetWidth, targetHeight)
                    onSnapshotReady(resizedBitmap)
                } else {
                    Log.e(TAG, "Failed to capture map snapshot: Bitmap is null")
                    onSnapshotReady(null)
                }
            }
        }
    }

    override fun clear() {
        cleanUp()
    }

    private fun cleanUp() {
        map.clear()
        currentUserMarker?.remove()
        currentUserMarker = null
        _map = null
    }

    private fun addPolyline(
        startLatLng: LatLng,
        endLatLng: LatLng,
    ) {
        val polylineOptions =
            PolylineOptions().color(polylineColor).width(POLYLINE_WIDTH).add(startLatLng, endLatLng)
        map.addPolyline(polylineOptions)
    }

    private fun adjustCameraToBounds(
        bounds: LatLngBounds,
        padding: Int,
    ) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, padding),
        )
        Log.d(TAG, "adjustCameraToBounds")
    }

    companion object {
        private const val ZOOM_LEVEL = 15.0f
        private const val POLYLINE_WIDTH = 7.5f

        private const val TAG = "MyStack: MapManager"
    }
}
