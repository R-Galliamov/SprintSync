package com.developers.sprintsync.data.track.service.processing.calculator

import android.location.Location
import com.developers.sprintsync.domain.track.model.LocationModel
import javax.inject.Inject

/**
 * Interface for calculating the distance between two geographical locations.
 */
interface DistanceCalculator {
    fun distM(l1: LocationModel, l2: LocationModel): Float
}


/**
 * A [DistanceCalculator] implementation that uses the Android [Location.distanceBetween] method
 * to calculate the distance between two geographical points.
 *
 * This class leverages the built-in Android framework for accurate distance calculations,
 * taking into account the Earth's curvature.
 *
 * @constructor Creates an instance of [AndLocDistCalculator]. This constructor is typically
 *              used with dependency injection frameworks like Hilt or Dagger.
 */
class AndLocDistCalculator @Inject constructor() : DistanceCalculator {
    override fun distM(l1: LocationModel, l2: LocationModel): Float {
        val distance = FloatArray(1)
        Location.distanceBetween(
            l1.lat.value,
            l1.lon.value,
            l2.lat.value,
            l2.lon.value,
            distance,
        )
        return distance[0]
    }
}