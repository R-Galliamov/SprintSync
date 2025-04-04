package com.developers.sprintsync.presentation.components

import com.developers.sprintsync.core.util.track_formatter.CaloriesFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.core.util.track_formatter.PaceFormatter
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.presentation.workout_session.active.util.polyline.PolylineProcessor
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject

data class TrackDisplayModel(
    val id: Int,
    val distanceUnit: String,
    val duration: String,
    val avgPace: String,
    val bestPace: String,
    val calories: String,
    val polylines: List<PolylineOptions>,
)

class TrackDisplayModelCreator
    @Inject
    constructor(
        private val polylineProcessor: PolylineProcessor,
    ) {
        fun create(track: Track): TrackDisplayModel {
            val distance = DistanceUiFormatter.format(track.distanceMeters, DistanceUiPattern.WITH_UNIT)
            val duration = DurationUiFormatter.format(track.durationMillis, DurationUiPattern.HH_MM_SS)
            val avgPace = PaceFormatter.formatPaceWithTwoDecimals(track.avgPace)
            val bestPace = PaceFormatter.formatPaceWithTwoDecimals(track.bestPace)
            val calories = CaloriesFormatter.formatCalories(track.calories)
            val polylines = polylineProcessor.generatePolylines(track.segments)
            return TrackDisplayModel(
                id = track.id,
                distanceUnit = distance,
                duration = duration,
                avgPace = avgPace,
                bestPace = bestPace,
                calories = calories,
                polylines = polylines,
            )
        }
    }
