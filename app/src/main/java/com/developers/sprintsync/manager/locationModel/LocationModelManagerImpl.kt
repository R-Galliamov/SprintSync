package com.developers.sprintsync.manager.locationModel

import android.location.Location
import com.developers.sprintsync.model.LocationModel

class LocationModelManagerImpl : LocationModelManager {
    override fun distanceBetween(start: LocationModel, end: LocationModel): Float {
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