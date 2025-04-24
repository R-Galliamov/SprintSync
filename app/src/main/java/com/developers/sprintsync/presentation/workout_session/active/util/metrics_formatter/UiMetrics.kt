package com.developers.sprintsync.presentation.workout_session.active.util.metrics_formatter

import com.developers.sprintsync.core.util.track_formatter.CaloriesUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.developers.sprintsync.core.util.track_formatter.PaceUiFormatter
import com.developers.sprintsync.domain.track.model.Segment

data class UiMetrics(
    val distance: String,
    val pace: String,
    val calories: String,
) {
    companion object {
        val INITIAL = create(0f, 0f, null)

        fun create(distanceMeters: Float, calories: Float, lastSegment: Segment?) =
            UiMetricsFormatter.format(distanceMeters, calories, lastSegment)
    }
}

object UiMetricsFormatter {
    fun format(distanceMeters: Float, calories: Float, lastSegment: Segment?): UiMetrics {
        val distanceString = DistanceUiFormatter.format(distanceMeters, DistanceUiPattern.PLAIN)
        val paceString = getPace(lastSegment)
        val caloriesString = CaloriesUiFormatter.format(calories, CaloriesUiFormatter.Pattern.PLAIN)
        return UiMetrics(
            distance = distanceString,
            pace = paceString,
            calories = caloriesString,
        )
    }

    private fun getPace(segment: Segment?): String {
        val pace = when (segment) {
            is Segment.Active -> PaceUiFormatter.format(segment.pace, PaceUiFormatter.Pattern.TWO_DECIMALS)
            else -> INVALID_PACE
        }
        return pace
    }

    private const val INVALID_PACE = "-"
}