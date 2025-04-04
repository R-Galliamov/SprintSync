package com.developers.sprintsync.presentation.workout_session.active.util.metrics_formatter

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.core.util.track_formatter.CaloriesFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.developers.sprintsync.core.util.track_formatter.PaceFormatter

object UiMetricsFormatter {
    fun format(track: Track): UiMetrics {
        val distance = DistanceUiFormatter.format(track.distanceMeters, DistanceUiPattern.PLAIN)
        val pace = getPace(track)
        val caloriesBurned = CaloriesFormatter.formatCalories(track.calories, false)
        return UiMetrics(
            distance = distance,
            pace = pace,
            caloriesBurned = caloriesBurned,
        )
    }

    private fun getPace(track: Track): String {
        val pace =
            when (val segment = track.segments.lastOrNull()) {
                is Segment.Active -> PaceFormatter.formatPaceWithTwoDecimals(segment.pace)
                else -> INVALID_PACE
            }
        return pace
    }

    private const val INVALID_PACE = "-"
}
