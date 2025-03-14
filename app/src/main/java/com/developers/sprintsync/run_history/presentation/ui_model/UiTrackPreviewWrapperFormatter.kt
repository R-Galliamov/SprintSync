package com.developers.sprintsync.run_history.presentation.ui_model

import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.CaloriesFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiPattern
import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewWrapper
import com.developers.sprintsync.core.util.formatter.DateFormatter

object UiTrackPreviewWrapperFormatter {
    fun format(tws: TrackPreviewWrapper): UiTrackPreviewWrapper {
        val track = tws.track
        val date = DateFormatter.formatDate(track.timestamp, DateFormatter.Pattern.DAY_MONTH_YEAR_WEEK_DAY)
        val distance = DistanceUiFormatter.format(track.distanceMeters, DistanceUiPattern.WITH_UNIT)
        val duration = DurationUiFormatter.format(track.durationMillis, DurationUiPattern.HH_MM_SS)
        val calories = CaloriesFormatter.formatCalories(track.calories)
        return UiTrackPreviewWrapper(
            id = track.id,
            date = date,
            distance = distance,
            duration = duration,
            calories = calories,
            previewPath = tws.preview?.filePath,
        )
    }

    fun format(list: List<TrackPreviewWrapper>): List<UiTrackPreviewWrapper> = list.map { format(it) }
}
