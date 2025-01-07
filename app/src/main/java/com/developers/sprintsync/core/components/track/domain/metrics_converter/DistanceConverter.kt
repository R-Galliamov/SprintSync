package com.developers.sprintsync.core.components.track.domain.metrics_converter

object DistanceConverter {
    fun metersToKilometers(distanceInMeters: Int): Float = distanceInMeters / (METERS_IN_KILOMETERS.toFloat())

    fun kilometersToMeters(distanceInKilometers: Float): Int = (distanceInKilometers * METERS_IN_KILOMETERS).toInt()

    private const val METERS_IN_KILOMETERS = 1000
}
