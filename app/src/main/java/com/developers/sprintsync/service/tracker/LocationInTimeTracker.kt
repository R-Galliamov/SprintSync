package com.developers.sprintsync.service.tracker

import com.developers.sprintsync.manager.location.LocationManager
import com.developers.sprintsync.model.tracking.LocationModel
import com.developers.sprintsync.model.tracking.toDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocationInTimeTracker @Inject constructor(
    private val locationManager: LocationManager
) {
    private var lastLocation: LocationModel? = null
    private var lastTimeStamp: Long = 0L

    fun locationFlow(): Flow<LocationModel> =
        locationManager.listenToLocation().map { location ->
            location.toDataModel()
        }

    fun assureLocationChanged(location: LocationModel): Boolean =
        (lastLocation == null || lastLocation != location)

    fun updateLastLocationTime(location: LocationModel, timeMillis: Long) {
        lastLocation = location
        lastTimeStamp = timeMillis
    }
}
