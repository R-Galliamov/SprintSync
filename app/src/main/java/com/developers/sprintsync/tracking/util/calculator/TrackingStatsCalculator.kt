package com.developers.sprintsync.tracking.util.calculator

import com.developers.sprintsync.tracking.model.indicator.TrackingStats
import com.developers.sprintsync.tracking.model.track.Track

class TrackingStatsCalculator {
    companion object {
        fun calculateStats(tracks: List<Track>): TrackingStats {
            if (tracks.isEmpty()) {
                return TrackingStats.EMPTY_TRACKING_STATS
            }

            return TrackingStats(
                runs = tracks.size,
                totalDistance = tracks.sumOf { it.distanceMeters },
                totalCalories = tracks.sumOf { it.calories },
                longestDistance = tracks.maxOf { it.distanceMeters },
                bestPace = tracks.minOf { it.bestPace },
                maxDuration = tracks.maxOf { it.durationMillis },
            )
        }
    }
}
