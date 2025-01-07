package com.developers.sprintsync.home.statistics.domain.util.creator

import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.home.statistics.domain.model.TracksStatsCompact
import com.developers.sprintsync.statistics.components.TracksStatsCalculator

class TracksStatsCompactCreator {
    fun createTracksStats(tracks: List<Track>): TracksStatsCompact {
        if (tracks.isEmpty()) return TracksStatsCompact.EMPTY

        val calculator = TracksStatsCalculator(tracks)
        return TracksStatsCompact(
            runs = calculator.numberOfWorkouts,
            totalDistance = calculator.totalDistanceMeters,
            totalCalories = calculator.totalCaloriesBurned,
            longestDistance = calculator.longestDistanceMeters,
            bestPace = calculator.bestPace,
            maxDuration = calculator.longestDurationMillis,
        )
    }
}
