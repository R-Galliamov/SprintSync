package com.developers.sprintsync.tracking.analytics.util.builder

import com.developers.sprintsync.tracking.data.model.indicator.TrackingStats
import com.developers.sprintsync.tracking.data.model.track.Track

class TrackingStatsBuilder {
    companion object {
        fun buildStats(tracks: List<Track>): TrackingStats {
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
