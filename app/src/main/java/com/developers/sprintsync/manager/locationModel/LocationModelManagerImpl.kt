package com.developers.sprintsync.manager.locationModel

import android.location.Location
import com.developers.sprintsync.model.tracking.LocationModel
import javax.inject.Inject

class LocationModelManagerImpl @Inject constructor() : LocationModelManager {
    override fun distanceBetweenInMeters(start: LocationModel, end: LocationModel): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude.value,
            start.longitude.value,
            end.latitude.value,
            end.longitude.value,
            results
        )
        return results[0]
    }
}
