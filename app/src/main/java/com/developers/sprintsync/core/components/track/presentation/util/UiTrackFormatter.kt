package com.developers.sprintsync.core.components.track.presentation.util

import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.CaloriesFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiPattern
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import com.developers.sprintsync.core.components.track.presentation.model.UiTrack
import com.developers.sprintsync.tracking_session.presentation.tracking.util.polyline.PolylineProcessor
import javax.inject.Inject

class UiTrackFormatter
    @Inject
    constructor(
        private val polylineProcessor: PolylineProcessor,
    ) {
        fun format(track: Track): UiTrack {
            val distance = DistanceUiFormatter.format(track.distanceMeters, DistanceUiPattern.WITH_UNIT)
            val duration = DurationUiFormatter.format(track.durationMillis, DurationUiPattern.HH_MM_SS)
            val avgPace = PaceFormatter.formatPaceWithTwoDecimals(track.avgPace)
            val bestPace = PaceFormatter.formatPaceWithTwoDecimals(track.bestPace)
            val calories = CaloriesFormatter.formatCalories(track.calories)
            val polylines = polylineProcessor.generatePolylines(track.segments)
            return UiTrack(
                id = track.id,
                distance = distance,
                duration = duration,
                avgPace = avgPace,
                bestPace = bestPace,
                calories = calories,
                polylines = polylines,
            )
        }
    }
