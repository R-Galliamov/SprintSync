package com.developers.sprintsync.presentation.workouts_stats

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.track_formatter.CaloriesUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.core.util.track_formatter.PaceUiFormatter
import com.developers.sprintsync.domain.statistics.model.WorkoutsStats
import com.developers.sprintsync.presentation.components.DistanceFormatter
import javax.inject.Inject

data class WorkoutsStatsUiModel(
    val totalWorkouts: String,
    val totalWorkoutDays: String,
    val totalDistance: String,
    val totalDuration: String,
    val longestDistance: String,
    val longestDuration: String,
    val avgPace: String,
    val peakPace: String,
    val totalCalories: String,
) {
    companion object {
        val EMPTY = WorkoutsStatsUiModel(
            totalWorkouts = "",
            totalWorkoutDays = "",
            totalDistance = "",
            totalDuration = "",
            longestDistance = "",
            longestDuration = "",
            avgPace = "",
            peakPace = "",
            totalCalories = "",
        )
    }
}

class WorkoutsStatsUiModelFormatter @Inject constructor(
    private val distanceFormatter: DistanceFormatter,
    private val log: AppLogger
) {

    fun format(stats: WorkoutsStats): WorkoutsStatsUiModel {
        return try {
            WorkoutsStatsUiModel(
                totalWorkouts = stats.totalWorkouts.toString(),
                totalWorkoutDays = stats.totalWorkoutDays.toString(),
                totalDistance = distanceFormatter.format(stats.totalDistanceMeters).value,
                totalDuration = DurationUiFormatter.format(stats.totalDurationMillis, DurationUiPattern.HH_MM_SS),
                longestDistance = distanceFormatter.format(stats.longestDistanceMeters).value,
                longestDuration = DurationUiFormatter.format(
                    stats.longestDurationMillis,
                    DurationUiPattern.HH_MM_SS
                ),
                avgPace = PaceUiFormatter.format(stats.avgPaceMPKm, PaceUiFormatter.Pattern.TWO_DECIMALS),
                peakPace = PaceUiFormatter.format(stats.peakPaceMPKm, PaceUiFormatter.Pattern.TWO_DECIMALS),
                totalCalories = CaloriesUiFormatter.format(stats.totalCalories, CaloriesUiFormatter.Pattern.PLAIN),
            )
        } catch (e: Exception) {
            log.e("Error creating WorkoutsStatsUiModel", e)
            WorkoutsStatsUiModel.EMPTY
        }
    }
}
