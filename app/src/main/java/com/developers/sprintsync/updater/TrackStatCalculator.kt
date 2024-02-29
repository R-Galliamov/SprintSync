package com.developers.sprintsync.updater

import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.model.TrackSegment
import javax.inject.Inject

class TrackStatCalculator
    @Inject
    constructor() {
        fun calculateDuration(
            track: Track,
            newSegment: TrackSegment,
        ): Long {
            return track.durationMillis + newSegment.durationMillis
        }

        fun calculateDistance(
            track: Track,
            newSegment: TrackSegment,
        ): Int {
            return track.distanceMeters + newSegment.distanceMeters
        }

        fun calculateAvgPace(
            track: Track,
            newSegment: TrackSegment,
        ): Float {
            return (track.avgPace + newSegment.pace) / 2
        }

        fun calculateMaxPace(
            track: Track,
            newSegment: TrackSegment,
        ): Float {
            return maxOf(track.maxPace, newSegment.pace)
        }

        fun calculateCalories(
            track: Track,
            newSegment: TrackSegment,
        ): Int {
            return track.calories + newSegment.burnedKCalories
        }
    }
