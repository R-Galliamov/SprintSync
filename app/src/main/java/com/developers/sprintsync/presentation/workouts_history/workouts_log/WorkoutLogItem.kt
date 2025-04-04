package com.developers.sprintsync.presentation.workouts_history.workouts_log

import com.developers.sprintsync.core.util.formatter.DateFormatter
import com.developers.sprintsync.core.util.track_formatter.CaloriesFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.domain.track_preview.model.TrackPreviewWrapper

data class WorkoutLogItem(
    val id: Int,
    val date: String,
    val distance: String,
    val duration: String,
    val calories: String,
    val previewPath: String?,
) {
    companion object {
        fun create(data: TrackPreviewWrapper) = Formatter.format(data)

        fun create(data: List<TrackPreviewWrapper>) = Formatter.format(data)
    }

    private object Formatter {
        fun format(tws: TrackPreviewWrapper): WorkoutLogItem {
            val track = tws.track
            val date = DateFormatter.formatDate(track.timestamp, DateFormatter.Pattern.DAY_MONTH_YEAR_WEEK_DAY)
            val distance = DistanceUiFormatter.format(track.distanceMeters, DistanceUiPattern.WITH_UNIT)
            val duration = DurationUiFormatter.format(track.durationMillis, DurationUiPattern.HH_MM_SS)
            val calories = CaloriesFormatter.formatCalories(track.calories)
            return WorkoutLogItem(
                id = track.id,
                date = date,
                distance = distance,
                duration = duration,
                calories = calories,
                previewPath = tws.preview?.filePath,
            )
        }

        fun format(list: List<TrackPreviewWrapper>): List<WorkoutLogItem> = list.map { format(it) }
    }
}
