package com.developers.sprintsync.core.components.track.domain.metrics_converter

// Replace with class
object DistanceConverter {
    fun metersToKilometers(distanceInMeters: Float): Float = distanceInMeters / (METERS_IN_KILOMETERS)

    // TODO replace with float
    fun kilometersToMeters(distanceInKilometers: Float): Int = (distanceInKilometers * METERS_IN_KILOMETERS).toInt()

    private const val METERS_IN_KILOMETERS = 1000f
}
