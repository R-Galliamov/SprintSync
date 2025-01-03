package com.developers.sprintsync.statistics.presentation.statistics.util.formatter

import com.developers.sprintsync.statistics.domain.statistics.WeeklyStatistics
import com.developers.sprintsync.tracking.analytics.dataManager.calculator.TrackStatisticsCalculator
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.CaloriesFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DistanceFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DurationFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.PaceFormatter
import com.developers.sprintsync.tracking.session.model.track.Track

class WeeklyStatisticsFormatter {
    fun formatWeeklyStatistics(tracks: List<Track>): WeeklyStatistics {
        if (tracks.isEmpty()) return WeeklyStatistics.EMPTY
        val calculator = TrackStatisticsCalculator(tracks)

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
            totalDistance = DistanceFormatter.metersToPresentableKilometers(totalDistance, true),
            totalDuration = DurationFormatter.formatToHhMmSs(totalDuration),
            bestDistance = DistanceFormatter.metersToPresentableKilometers(bestDistance, true),
            bestDuration = DurationFormatter.formatToHhMmSs(bestDuration),
            avgPace = PaceFormatter.formatPaceWithTwoDecimals(avgPace),
            bestPace = PaceFormatter.formatPaceWithTwoDecimals(bestPace),
            totalCalories = CaloriesFormatter.formatCalories(totalCalories, false),
        )
    }

    companion object {
        private const val WORKOUT_DAYS_FORMAT = "%1\$d/7"
    }
}
