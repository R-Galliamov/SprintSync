package com.developers.sprintsync.tracking_session.presentation.util.formatter

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.CaloriesFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.tracking_session.presentation.model.UiMetrics

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
                else-> INVALID_PACE
            }
        return pace
    }

    private const val INVALID_PACE = "-"
}
