package com.developers.sprintsync.presentation.workouts_statistics.data

import com.developers.sprintsync.core.util.track_formatter.CaloriesFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.core.util.track_formatter.PaceFormatter
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.presentation.components.TracksStatsCalculator

data class GeneralStatistics(
    val totalWorkouts: String,
    val maxWorkoutStreak: String,
    val totalWorkoutDays: String,
    val totalDistance: String,
    val totalDuration: String,
    val avgPace: String,
    val bestPace: String,
    val totalCalories: String,
) {
    companion object {
        fun create(tracks: List<Track>): GeneralStatistics = Formatter.format(tracks)

        val EMPTY =
            GeneralStatistics(
                totalWorkouts = "",
                maxWorkoutStreak = "",
                totalWorkoutDays = "",
                totalDistance = "",
                totalDuration = "",
                avgPace = "",
                bestPace = "",
                totalCalories = "",
            )
    }

    private object Formatter {
        fun format(tracks: List<Track>): GeneralStatistics {
            if (tracks.isEmpty()) return EMPTY
            val calculator = TracksStatsCalculator(tracks)

            val totalWorkouts = calculator.totalWorkouts
            val maxWorkoutStreak = calculator.maxWorkoutStreak
            val totalWorkoutDays = calculator.workoutDays
            val totalDistance = calculator.totalDistanceMeters
            val totalDuration = calculator.totalDurationMillis
            val avgPace = calculator.averagePace
            val bestPace = calculator.bestPace
            val totalCalories = calculator.totalCaloriesBurned

            return GeneralStatistics(
                totalWorkouts = totalWorkouts.toString(),
                maxWorkoutStreak = maxWorkoutStreak.toString(),
                totalWorkoutDays = totalWorkoutDays.toString(),
                totalDistance = DistanceUiFormatter.format(totalDistance, DistanceUiPattern.WITH_UNIT),
                totalDuration = DurationUiFormatter.format(totalDuration, DurationUiPattern.HH_MM_SS),
                avgPace = PaceFormatter.formatPaceWithTwoDecimals(avgPace),
                bestPace = PaceFormatter.formatPaceWithTwoDecimals(bestPace),
                totalCalories = CaloriesFormatter.formatCalories(totalCalories, false),
            )
        }
    }
}
