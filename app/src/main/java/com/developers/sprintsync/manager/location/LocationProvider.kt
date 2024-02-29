package com.developers.sprintsync.manager.location

import com.developers.sprintsync.tracking.model.LocationModel
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun listenToLocation(): Flow<LocationModel>

    fun hasLocationPermission(): Boolean
}
