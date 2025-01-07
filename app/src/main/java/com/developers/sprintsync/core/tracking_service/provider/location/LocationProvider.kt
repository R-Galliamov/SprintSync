package com.developers.sprintsync.core.tracking_service.provider.location

import com.developers.sprintsync.core.tracking_service.data.model.location.LocationModel
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun listenToLocation(): Flow<LocationModel>

    suspend fun getLocation(): LocationModel

    fun hasLocationPermission(): Boolean
}
