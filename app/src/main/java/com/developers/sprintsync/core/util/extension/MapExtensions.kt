package com.developers.sprintsync.core.util.extension

import android.content.Context
import android.util.Log
import com.developers.sprintsync.presentation.components.MapStyle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

fun List<LatLng>.toLatLngBounds(): LatLngBounds {
    require(this.isNotEmpty()) { "Cannot create LatLngBounds from an empty list" }
    val bounds = LatLngBounds.Builder()
    this.forEach { bounds.include(it) }
    return bounds.build()
}

fun GoogleMap.adjustCameraToBounds(
    bounds: LatLngBounds,
    padding: Int,
) {
    val update = CameraUpdateFactory.newLatLngBounds(bounds, padding)
    this.moveCamera(update)
}

fun GoogleMap.updateCameraPosition(
    target: LatLng,
    zoom: Float,
    animated: Boolean,
) {
    val cameraFactory = CameraUpdateFactory.newLatLngZoom(target, zoom)
    if (animated) {
        this.animateCamera(cameraFactory)
    } else {
        this.moveCamera(cameraFactory)
    }
}

fun GoogleMap.setMapStyle(
    context: Context,
    mapStyle: MapStyle,
    tag: String = "Google Map"
) {
    try {
        val success = this.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, mapStyle.styleResId))
        if (!success) Log.e(tag, "Error: Failed to apply map style.")
    } catch (e: Exception) {
        Log.e(tag, "Error: Failed to load map style", e)
    }
}
