package com.developers.sprintsync.tracking.analytics.dataManager.builder

import com.developers.sprintsync.tracking.analytics.dataManager.calculator.TrackStatisticsCalculator
import com.developers.sprintsync.tracking.analytics.model.TrackingStatistics
import com.developers.sprintsync.tracking.session.model.track.Track

class TrackStatisticsCompactFormatter {
    fun formatCompactStatistics(tracks: List<Track>): TrackingStatistics {
        if (tracks.isEmpty()) return TrackingStatistics.EMPTY

        val calculator = TrackStatisticsCalculator(tracks)
        return TrackingStatistics(
            runs = calculator.numberOfWorkouts,
            totalDistance = calculator.totalDistanceMeters,
            totalCalories = calculator.totalCaloriesBurned,
            longestDistance = calculator.longestDistanceMeters,
            bestPace = calculator.bestPace,
            maxDuration = calculator.longestDurationMillis,
        )
    }
}
