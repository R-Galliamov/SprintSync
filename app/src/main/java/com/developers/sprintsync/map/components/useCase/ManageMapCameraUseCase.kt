package com.developers.sprintsync.map.components.useCase

import com.developers.sprintsync.map.components.presentation.MapManager
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
