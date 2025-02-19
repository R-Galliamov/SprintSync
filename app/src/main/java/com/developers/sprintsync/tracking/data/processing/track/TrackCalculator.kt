package com.developers.sprintsync.tracking.data.processing.track

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Track
import javax.inject.Inject

class TrackCalculator
    @Inject
    constructor() {
        fun calculateDuration(
            track: Track,
            newSegment: Segment,
        ): Long = track.durationMillis + newSegment.durationMillis

        fun calculateDistance(
            track: Track,
            newSegment: Segment,
        ): Float =
            when (newSegment) {
                is Segment.Active -> track.distanceMeters + newSegment.distanceMeters
                is Segment.Stationary -> track.distanceMeters
            }

        fun calculateAvgPace(
            track: Track,
            newSegment: Segment,
        ): Float =
            when (newSegment) {
                is Segment.Active -> (track.avgPace + newSegment.pace) / 2
                is Segment.Stationary -> track.avgPace
            }

        fun calculateBestPace(
            track: Track,
            newSegment: Segment,
        ): Float =
            when (newSegment) {
                is Segment.Active -> minOf(track.bestPace, newSegment.pace)
                is Segment.Stationary -> track.bestPace
            }

        fun calculateCalories(
            track: Track,
            newSegment: Segment,
        ): Int =
            when (newSegment) {
                is Segment.Active -> track.calories + newSegment.calories
                is Segment.Stationary -> track.calories
            }
    }
