package com.developers.sprintsync.tracking_session.presentation.tracking.util.map

import com.developers.sprintsync.core.util.extension.updateCameraPosition
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

class MapCameraManager {
    private var isFirstUpdate = true
    private var map: GoogleMap? = null

    fun attachMap(map: GoogleMap) {
        this.map = map
    }

    fun moveCamera(target: LatLng) {
        map?.updateCameraPosition(target, DEFAULT_ZOOM, shouldAnimateCamera())
    }

    fun detachMap() {
        map = null
    }

    private fun shouldAnimateCamera(): Boolean {
        val animate = !isFirstUpdate
        isFirstUpdate = false
        return animate
    }

    companion object {
        const val DEFAULT_ZOOM = 15f
    }
}
