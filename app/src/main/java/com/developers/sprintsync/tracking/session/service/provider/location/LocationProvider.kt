package com.developers.sprintsync.tracking.session.service.provider.location

import com.developers.sprintsync.tracking.session.model.track.LocationModel
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun listenToLocation(): Flow<LocationModel>

    suspend fun getLocation(): LocationModel

    fun hasLocationPermission(): Boolean
}
