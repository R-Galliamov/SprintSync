package com.developers.sprintsync.tracking.analytics.dataManager.builder

import com.developers.sprintsync.tracking.analytics.model.TrackingStatistics
import com.developers.sprintsync.tracking.session.model.track.Track

class TrackingStatsBuilder {
    companion object {
        fun buildStats(tracks: List<Track>): TrackingStatistics {
            if (tracks.isEmpty()) {
                return TrackingStatistics.EMPTY_TRACKING_STATS
            }

            return TrackingStatistics(
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
