package com.developers.sprintsync.statistics.ui.statistics.util.formatter

import com.developers.sprintsync.statistics.model.statistics.GeneralStatistics
import com.developers.sprintsync.tracking.analytics.dataManager.calculator.TrackStatisticsCalculator
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.CaloriesFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DistanceFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DurationFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.PaceFormatter
import com.developers.sprintsync.tracking.session.model.track.Track

class GeneralStatisticsFormatter {
    fun formatStatistics(tracks: List<Track>): GeneralStatistics {
        if (tracks.isEmpty()) return GeneralStatistics.EMPTY
        val calculator = TrackStatisticsCalculator(tracks)

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
            totalDistance = DistanceFormatter.metersToPresentableKilometers(totalDistance, true),
            totalDuration = DurationFormatter.formatToHhMmSs(totalDuration),
            avgPace = PaceFormatter.formatPaceWithTwoDecimals(avgPace),
            bestPace = PaceFormatter.formatPaceWithTwoDecimals(bestPace),
            totalBurnedKcal = CaloriesFormatter.formatCalories(totalCalories, false),
        )
    }
}
