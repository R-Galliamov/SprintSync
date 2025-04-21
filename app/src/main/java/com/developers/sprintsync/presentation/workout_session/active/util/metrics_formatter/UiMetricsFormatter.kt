package com.developers.sprintsync.presentation.workout_session.active.util.metrics_formatter

import com.developers.sprintsync.core.util.track_formatter.CaloriesUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.developers.sprintsync.core.util.track_formatter.PaceUiFormatter
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track

object UiMetricsFormatter {
    fun format(track: Track): UiMetrics {
        val distance = DistanceUiFormatter.format(track.distanceMeters, DistanceUiPattern.PLAIN)
        val pace = getPace(track)
        val caloriesBurned = CaloriesUiFormatter.format(track.calories, CaloriesUiFormatter.Pattern.PLAIN)
        return UiMetrics(
            distance = distance,
            pace = pace,
            caloriesBurned = caloriesBurned,
        )
    }

    private fun getPace(track: Track): String {
        val pace =
            when (val segment = track.segments.lastOrNull()) {
                is Segment.Active -> PaceUiFormatter.format(segment.pace, PaceUiFormatter.Pattern.TWO_DECIMALS)
                else -> INVALID_PACE
            }
        return pace
    }

    private const val INVALID_PACE = "-"
}
