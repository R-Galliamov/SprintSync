package com.developers.sprintsync.statistics.presentation.util.formatter

import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.CaloriesFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiPattern
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import com.developers.sprintsync.statistics.components.TracksStatsCalculator
import com.developers.sprintsync.statistics.presentation.model.GeneralStatistics

class GeneralStatisticsFormatter {
    fun formatStatistics(tracks: List<Track>): GeneralStatistics {
        if (tracks.isEmpty()) return GeneralStatistics.EMPTY
        val calculator = TracksStatsCalculator(tracks)

        val totalWorkouts = calculator.numberOfWorkouts
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
            totalBurnedKcal = CaloriesFormatter.formatCalories(totalCalories, false),
        )
    }
}
