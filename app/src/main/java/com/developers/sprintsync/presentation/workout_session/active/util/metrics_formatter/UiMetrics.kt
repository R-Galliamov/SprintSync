package com.developers.sprintsync.presentation.workout_session.active.util.metrics_formatter

import com.developers.sprintsync.core.util.track_formatter.CaloriesUiFormatter
import com.developers.sprintsync.core.util.track_formatter.PaceUiFormatter
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.presentation.components.DistanceFormatter
import javax.inject.Inject

/**
 * Data model for displaying workout metrics in the UI.
 */
data class UiMetrics(
    val distance: String,
    val pace: String,
    val calories: String,
)

/**
 * Formats raw workout data into [UiMetrics] for UI display.
 * // TODO inject and log
 */
class UiMetricsFormatter @Inject constructor(private val distanceFormatter: DistanceFormatter) {

    /**
     * Formats workout data into a [UiMetrics] object.
     * @param distanceMeters The total distance in meters.
     * @param calories The total calories burned.
     * @param lastSegment The last [Segment] for pace calculation, or null if unavailable.
     * @return A formatted [UiMetrics] instance.
     */
    fun format(distanceMeters: Float, calories: Float, lastSegment: Segment?): UiMetrics {
        val distanceString = distanceFormatter.format(distanceMeters).value
        val paceString = getPace(lastSegment)
        val caloriesString = CaloriesUiFormatter.format(calories, CaloriesUiFormatter.Pattern.PLAIN)
        return UiMetrics(
            distance = distanceString,
            pace = paceString,
            calories = caloriesString,
        )
    }

    // Retrieves pace from the last segment or returns invalid pace
    private fun getPace(segment: Segment?): String {
        val pace = when (segment) {
            is Segment.Active -> PaceUiFormatter.format(segment.pace, PaceUiFormatter.Pattern.TWO_DECIMALS)
            else -> INVALID_PACE
        }
        return pace
    }

    companion object {
        private const val INVALID_PACE = "-"
    }
}