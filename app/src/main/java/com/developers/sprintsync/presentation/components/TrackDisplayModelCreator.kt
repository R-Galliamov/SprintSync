package com.developers.sprintsync.presentation.components

import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.presentation.components.formatter.CaloriesUiFormatter
import com.developers.sprintsync.presentation.components.formatter.DistanceFormatter
import com.developers.sprintsync.presentation.components.formatter.DurationUiFormatter
import com.developers.sprintsync.presentation.components.formatter.DurationUiPattern
import com.developers.sprintsync.presentation.components.formatter.PaceUiFormatter
import com.developers.sprintsync.presentation.workout_session.active.util.polyline.PolylineProcessor
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject

/**
 * Data model for displaying track details in the UI.
 */
data class TrackDisplayModel(
    val id: Int,
    val distanceUnit: String,
    val duration: String,
    val avgPace: String,
    val bestPace: String,
    val calories: String,
    val polylines: List<PolylineOptions>,
)

/**
 * Creates a [TrackDisplayModel] from a [Track] for UI display.
 */
class TrackDisplayModelCreator
@Inject
constructor(
    private val polylineProcessor: PolylineProcessor,
    private val distanceFormatter: DistanceFormatter,
) {

    /**
     * Converts a [Track] into a [TrackDisplayModel] with formatted UI data.
     * @param track The [Track] to convert.
     * @return The formatted [TrackDisplayModel].
     */
    fun create(track: Track): TrackDisplayModel {
        val distance = distanceFormatter.format(track.distanceMeters).withUnit
        val duration = DurationUiFormatter.format(track.durationMillis, DurationUiPattern.HH_MM_SS)
        val avgPace = formatPace(track.avgPace)
        val bestPace = formatPace(track.bestPace)
        val calories = CaloriesUiFormatter.format(track.calories, CaloriesUiFormatter.Pattern.PLAIN)
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


    private fun formatPace(pace: Float?): String {
        return if (pace == null) EMPTY_VALUE else PaceUiFormatter.format(
            pace,
            PaceUiFormatter.Pattern.TWO_DECIMALS
        )
    }

    companion object {
        const val EMPTY_VALUE = "-"
    }
}
