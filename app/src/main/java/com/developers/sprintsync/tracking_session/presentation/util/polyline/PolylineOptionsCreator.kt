package com.developers.sprintsync.tracking_session.presentation.util.polyline

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject

class PolylineOptionsCreator @Inject constructor(){
    companion object {
        private val polylineColor = Color.GREEN // TODO replace color
    }

    fun createPolylineOptions(polylines: List<List<LatLng>>): PolylineOptions {
        val polylineOptions = PolylineOptions().color(polylineColor).width(7.5f) // TODO update constants
        polylines.onEach { polyline ->
            polylineOptions.addAll(polyline) // TODO check if it saves gaps in paused state
        }
        return polylineOptions
    }
}