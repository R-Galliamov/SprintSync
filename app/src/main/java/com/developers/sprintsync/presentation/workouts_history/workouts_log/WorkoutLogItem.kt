package com.developers.sprintsync.presentation.workouts_history.workouts_log

import com.developers.sprintsync.presentation.components.formatter.DateFormatter
import com.developers.sprintsync.presentation.components.formatter.CaloriesUiFormatter
import com.developers.sprintsync.presentation.components.formatter.DurationUiFormatter
import com.developers.sprintsync.presentation.components.formatter.DurationUiPattern
import com.developers.sprintsync.data.track_preview.model.TrackWithPreview
import com.developers.sprintsync.presentation.components.formatter.DistanceFormatter
import javax.inject.Inject

data class WorkoutLogItem(
    val id: Int,
    val date: String,
    val distance: String,
    val duration: String,
    val calories: String,
    val previewPath: String?,
) {
    companion object {
        val EMPTY = WorkoutLogItem(
            id = 0,
            date = "",
            distance = "",
            duration = "",
            calories = "",
            previewPath = null
        )
    }

}

class WorkoutLogItemFormatter @Inject constructor(private val distanceFormatter: DistanceFormatter) {
    fun format(tws: TrackWithPreview): WorkoutLogItem {
        try {
            val track = tws.track
            val date = DateFormatter.formatDate(track.timestamp, DateFormatter.Pattern.DAY_MONTH_YEAR_WEEK_DAY)
            val distance = distanceFormatter.format(tws.track.distanceMeters).withUnit
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
        } catch (e: Exception) {
            return WorkoutLogItem.EMPTY
        }
    }

    fun format(list: List<TrackWithPreview>): List<WorkoutLogItem> = list.map { format(it) }
}
