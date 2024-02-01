package com.developers.sprintsync.manager.locationModel

import com.developers.sprintsync.model.LocationModel

interface LocationModelManager {
    fun distanceBetween(start: LocationModel, end: LocationModel) : Float
}