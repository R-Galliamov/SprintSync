package com.developers.sprintsync.manager.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationManager {
    fun listenToLocation(): Flow<Location>
    fun hasLocationPermission(): Boolean
}
