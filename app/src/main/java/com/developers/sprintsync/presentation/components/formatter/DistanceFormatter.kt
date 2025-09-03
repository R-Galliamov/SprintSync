package com.developers.sprintsync.presentation.components.formatter

import com.developers.sprintsync.core.util.DistanceConverter
import com.developers.sprintsync.core.util.extension.roundedDownNearestTen
import com.developers.sprintsync.core.util.log.AppLogger
import java.util.Locale
import javax.inject.Inject

/**
 * Formats distance values (in meters) for consistent display across the app.
 */
data class FormattedDistance(
    val value: String,
    val unit: String,
) {
    /**
     * Combines value and unit for display (e.g., "5.00 km").
     */
    val withUnit: String
        get() = String.format(Locale.getDefault(), JOINING_PATTERN, value, unit)

    companion object {
        val EMPTY = FormattedDistance("", "")
        private const val JOINING_PATTERN = "%s %s"
    }
}

interface DistanceFormatter {
    fun format(distanceInMeters: Float): FormattedDistance
}

/**
 * Creates [FormattedDistance] instances from meter values.
 */
class KilometerFormatter @Inject constructor(
    private val distanceConverter: DistanceConverter,
    private val log: AppLogger?,
) : DistanceFormatter {
    /**
     * Creates a [FormattedDistance] from meters, converting to kilometers.
     * @param distanceInMeters Distance in meters.
     * @return [FormattedDistance] with formatted value and unit.
     */
    override fun format(
        distanceInMeters: Float,
    ): FormattedDistance {
        try {
            require(distanceInMeters.isFinite() && distanceInMeters >= 0) { "Distance must be non-negative and finite" }
            val meters = distanceInMeters.roundedDownNearestTen().toFloat()
            val km = distanceConverter.convert(meters, DistanceConverter.Unit.M, DistanceConverter.Unit.KM)
            return FormattedDistance(km.toKmString(), KM_UNIT)
        } catch (e: Exception) {
            log?.e("Error creating DistanceDisplay: ${e.message}", e)
            return FormattedDistance.EMPTY
        }
    }

    private fun Float.toKmString(): String =
        String.format(Locale.getDefault(), KM_VALUE_PATTERN, this)


    companion object {
        private const val KM_VALUE_PATTERN = "%.2f"
        private const val KM_UNIT = "km"
    }
}