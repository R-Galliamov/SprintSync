package com.developers.sprintsync.statistics.presentation.util.formatter

import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.CaloriesFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiPattern
import com.developers.sprintsync.statistics.components.TracksStatsCalculator
import com.developers.sprintsync.statistics.presentation.model.WeeklyStatistics

class WeeklyStatisticsFormatter {
    fun formatWeeklyStatistics(tracks: List<Track>): WeeklyStatistics {
        if (tracks.isEmpty()) return WeeklyStatistics.EMPTY
        val calculator = TracksStatsCalculator(tracks)

        val workouts = calculator.numberOfWorkouts
        val workoutDaysFormatted = WORKOUT_DAYS_FORMAT.format(calculator.workoutDays)
        val totalDistance = calculator.totalDistanceMeters
        val totalDuration = calculator.totalDurationMillis
        val bestDistance = calculator.longestDistanceMeters
        val bestDuration = calculator.longestDurationMillis
        val avgPace = calculator.averagePace
        val bestPace = calculator.bestPace
        val totalCalories = calculator.totalCaloriesBurned

        return WeeklyStatistics(
            workouts = workouts.toString(),
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

    companion object {
        private const val WORKOUT_DAYS_FORMAT = "%1\$d/7"
    }
}
