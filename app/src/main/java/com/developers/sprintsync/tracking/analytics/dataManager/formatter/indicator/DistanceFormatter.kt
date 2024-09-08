package com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator

import com.developers.sprintsync.global.util.extension.roundedDownNearestTen
import com.developers.sprintsync.tracking.analytics.dataManager.calculator.DistanceCalculator
import java.util.Locale

class DistanceFormatter {
    companion object {
        // TODO based on setting mapper can decide what function to use to convert values
        // TODO init locale whit HILT?
        fun metersToPresentableKilometers(
            distanceInMeters: Int,
            includeUnit: Boolean = false,
        ): String {
            val roundedMeters = distanceInMeters.roundedDownNearestTen()
            val kilometers = DistanceCalculator.metersToKilometers(roundedMeters)
            val locale = Locale.getDefault()
            val formattedKilometers = String.format(locale, "%.2f", kilometers)
            return if (includeUnit) {
                val units = "km"
                "$formattedKilometers $units"
            } else {
                formattedKilometers
            }
        }
    }
}
