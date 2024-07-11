package com.developers.sprintsync.tracking.util.mapper.indicator

import com.developers.sprintsync.global.util.extension.roundedDownNearestTen
import java.util.Locale

class DistanceMapper {
    companion object {
        private const val METERS_IN_KILOMETERS = 1000

        // TODO based on setting mapper can decide what function to use to convert values
        // TODO init locale whit HILT?
        fun metersToPresentableKilometers(
            distanceInMeters: Int,
            includeUnit: Boolean = false,
        ): String {
            val roundedMeters = distanceInMeters.roundedDownNearestTen()
            val kilometers = metersToKilometers(roundedMeters)
            val locale = Locale.getDefault()
            val formattedKilometers = String.format(locale, "%.2f", kilometers)
            return if (includeUnit) {
                val units = "km"
                "$formattedKilometers $units"
            } else {
                formattedKilometers
            }
        }

        fun metersToKilometers(distanceInMeters: Int): Float = distanceInMeters / (METERS_IN_KILOMETERS.toFloat())
    }
}
