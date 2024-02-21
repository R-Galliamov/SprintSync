package com.developers.sprintsync.manager.locationModel

import com.developers.sprintsync.model.tracking.LocationModel

interface LocationModelManager {
    fun distanceBetweenInMeters(start: LocationModel, end: LocationModel): Float
}
