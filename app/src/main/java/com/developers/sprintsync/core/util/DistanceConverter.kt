package com.developers.sprintsync.core.util

import com.developers.sprintsync.core.util.log.AppLogger
import javax.inject.Inject

/**
 * Converts distances between meters and kilometers.
 */
class DistanceConverter @Inject constructor(
    private val log: AppLogger,
) {

    enum class Unit {
        KM, M
    }

    /**
     * Converts a distance from one unit to another.
     * @param from Distance value to convert.
     * @param unit Source unit (e.g., KM, M).
     * @param to Target unit (e.g., KM, M).
     * @return Converted distance in the target unit.
     * @throws IllegalArgumentException if the input is invalid or units are unsupported.
     */
    fun convert(from: Float, unit: Unit, to: Unit): Float {
        require(from.isFinite() && from >= 0) { "Distance must be non-negative and finite" }
        return when {
            unit == Unit.KM && to == Unit.M -> {
                val result = from * METERS_IN_KILOMETERS
                result
            }

            unit == Unit.M && to == Unit.KM -> {
                val result = from / METERS_IN_KILOMETERS
                result
            }

            unit == to -> from
            else -> {
                val error = "Unsupported unit conversion: $unit to $to"
                log.e(error)
                throw IllegalArgumentException(error)
            }
        }
    }

    companion object {
        private const val METERS_IN_KILOMETERS = 1000f
    }
}