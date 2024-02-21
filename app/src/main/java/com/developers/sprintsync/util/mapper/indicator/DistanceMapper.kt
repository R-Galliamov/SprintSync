package com.developers.sprintsync.util.mapper.indicator

import java.util.Locale

object DistanceMapper {

    private const val METERS_IN_KILOMETERS = 1000

    // TODO based on setting mapper can decide what function to use to convert values
    // TODO init locale whit HILT
    fun metersToPresentableDistance(distanceInMeters: Int): String {
        val kilometers = metersToKilometers(distanceInMeters)
        val locale = Locale.getDefault()
        return String.format(locale, "%.2f", kilometers)
    }

    fun metersToKilometers(distanceInMeters: Int): Float {
        return distanceInMeters / (METERS_IN_KILOMETERS.toFloat())
    }
}
