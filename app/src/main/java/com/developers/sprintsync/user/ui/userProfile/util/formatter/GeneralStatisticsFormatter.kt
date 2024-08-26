package com.developers.sprintsync.user.ui.userProfile.util.formatter

import com.developers.sprintsync.tracking.analytics.dataManager.calculator.TrackStatisticsCalculator
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.CaloriesFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DistanceFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DurationFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.PaceFormatter
import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.statistics.GeneralStatistics

class GeneralStatisticsFormatter {
    fun formatStatistics(tracks: List<Track>): GeneralStatistics {
        if (tracks.isEmpty()) return GeneralStatistics.EMPTY
        val calculator = TrackStatisticsCalculator(tracks)

        val maxWorkoutStreak = calculator.numberOfWorkouts
        val totalWorkoutDays = calculator.workoutDays
        val totalDistance = calculator.totalDistanceMeters
        val totalDuration = calculator.totalDurationMillis
        val avgPace = calculator.averagePace
        val bestPace = calculator.bestPace
        val totalCalories = calculator.totalCaloriesBurned

        return GeneralStatistics(
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
