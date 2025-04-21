package com.developers.sprintsync.presentation.workouts_history.workouts_log

import com.developers.sprintsync.core.util.formatter.DateFormatter
import com.developers.sprintsync.core.util.track_formatter.CaloriesUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.data.track_preview.model.TrackWithPreview

data class WorkoutLogItem(
    val id: Int,
    val date: String,
    val distance: String,
    val duration: String,
    val calories: String,
    val previewPath: String?,
) {
    companion object {
        fun create(data: TrackWithPreview) = Formatter.format(data)

        fun create(data: List<TrackWithPreview>) = Formatter.format(data)
    }

    private object Formatter {
        fun format(tws: TrackWithPreview): WorkoutLogItem {
            val track = tws.track
            val date = DateFormatter.formatDate(track.timestamp, DateFormatter.Pattern.DAY_MONTH_YEAR_WEEK_DAY)
            val distance = DistanceUiFormatter.format(track.distanceMeters, DistanceUiPattern.WITH_UNIT)
            val duration = DurationUiFormatter.format(track.durationMillis, DurationUiPattern.HH_MM_SS)
            val calories = CaloriesUiFormatter.format(track.calories, CaloriesUiFormatter.Pattern.PLAIN)
            return WorkoutLogItem(
                id = track.id,
                date = date,
                distance = distance,
                duration = duration,
                calories = calories,
                previewPath = tws.preview?.bitmapPath,
            )
        }

        fun format(list: List<TrackWithPreview>): List<WorkoutLogItem> = list.map { format(it) }
    }
}
