package com.developers.sprintsync.presentation.workout_session.active.util.map

import com.developers.sprintsync.core.util.extension.updateCameraPosition
import com.developers.sprintsync.core.util.log.AppLogger
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

/**
 * Manages the Google Map camera position and updates.
 */
@FragmentScoped
class MapCameraManager @Inject constructor(private val log: AppLogger) {
    private var isFirstUpdate = true
    private var map: GoogleMap? = null

    /**
     * Attaches the manager to a Google Map instance.
     * @param map The [GoogleMap] to control.
     */
    fun attachToMap(map: GoogleMap) {
        this.map = map
        log.i("Attached to map")
    }

    /**
     * Moves the map camera to a target location.
     * @param target The [LatLng] to center the camera on.
     */
    fun moveCamera(target: LatLng) {
        try {
            map?.updateCameraPosition(target, DEFAULT_ZOOM, shouldAnimate())
            log.d("Camera moved to: $target")
        } catch (e: Exception) {
            log.e("Failed to move camera: ${e.message}", e)
        }
    }

    // Detaches the manager from the map
    fun detachMap() {
        map = null
        log.i("Detached from map")
    }

    // Determines if the camera should animate based on first update
    private fun shouldAnimate(): Boolean {
        val animate = !isFirstUpdate
        isFirstUpdate = false
        return animate
    }

    companion object {
        const val DEFAULT_ZOOM = 20f
    }
}
