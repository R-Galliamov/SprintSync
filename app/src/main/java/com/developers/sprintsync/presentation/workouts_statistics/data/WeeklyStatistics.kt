package com.developers.sprintsync.presentation.workouts_statistics.data

import com.developers.sprintsync.core.util.track_formatter.CaloriesFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.core.util.track_formatter.PaceFormatter
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.presentation.components.TracksStatsCalculator

data class WeeklyStatistics(
    val workouts: String,
    val workoutDays: String,
    val totalDistance: String,
    val totalDuration: String,
    val bestDistance: String,
    val bestDuration: String,
    val avgPace: String,
    val bestPace: String,
    val totalCalories: String,
) {
    companion object {
        fun create(tracks: List<Track>): WeeklyStatistics = Formatter.format(tracks)

        val EMPTY =
            WeeklyStatistics(
                workouts = "",
                workoutDays = "",
                totalDistance = "",
                totalDuration = "",
                bestDistance = "",
                bestDuration = "",
                avgPace = "",
                bestPace = "",
                totalCalories = "",
            )
    }

    private object Formatter {
        fun format(tracks: List<Track>): WeeklyStatistics {
            if (tracks.isEmpty()) return EMPTY
            val calculator = TracksStatsCalculator(tracks)

            val totalWorkouts = calculator.totalWorkouts
            val workoutDaysFormatted = WORKOUT_DAYS_FORMAT.format(calculator.workoutDays)
            val totalDistance = calculator.totalDistanceMeters
            val totalDuration = calculator.totalDurationMillis
            val bestDistance = calculator.longestDistanceMeters
            val bestDuration = calculator.longestDurationMillis
            val avgPace = calculator.averagePace
            val bestPace = calculator.bestPace
            val totalCalories = calculator.totalCaloriesBurned

            return WeeklyStatistics(
                workouts = totalWorkouts.toString(),
                workoutDays = workoutDaysFormatted,
                totalDistance = DistanceUiFormatter.format(totalDistance, DistanceUiPattern.WITH_UNIT),
                totalDuration = DurationUiFormatter.format(totalDuration, DurationUiPattern.HH_MM_SS),
                bestDistance = DistanceUiFormatter.format(bestDistance, DistanceUiPattern.WITH_UNIT),
                bestDuration = DurationUiFormatter.format(bestDuration, DurationUiPattern.HH_MM_SS),
                avgPace = PaceFormatter.formatPaceWithTwoDecimals(avgPace),
                bestPace = PaceFormatter.formatPaceWithTwoDecimals(bestPace),
                totalCalories = CaloriesFormatter.formatCalories(totalCalories, false),
            )
        }

        private const val WORKOUT_DAYS_FORMAT = "%1\$d/7"
    }
}
