package com.developers.sprintsync.tracking.session.ui.useCase

import com.developers.sprintsync.tracking.analytics.ui.map.util.MapManager
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class ManageMapCameraUseCase
    @Inject
    constructor() {
        private var isFirstLocationUpdate = true

        operator fun invoke(
            mapManager: MapManager,
            latLng: LatLng,
        ) {
            mapManager.moveCameraToLocation(latLng, !isFirstLocationUpdate)
            isFirstLocationUpdate = false
        }
    }
