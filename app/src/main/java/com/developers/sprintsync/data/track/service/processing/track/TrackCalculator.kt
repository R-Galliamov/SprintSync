package com.developers.sprintsync.data.track.service.processing.track

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track
import javax.inject.Inject

class TrackCalculator @Inject constructor() {
    fun updateTrackWithSegment(
        track: Track,
        segment: Segment,
    ): Track {
        val duration = calculateDuration(track, segment)
        val distance = calculateDistance(track, segment)
        val avgPace = calculateAvgPace(track, segment)
        val bestPace = calculateBestPace(track, segment)
        val calories = calculateCalories(track, segment)
        val segments = track.segments + segment
        return track.copy(
            durationMillis = duration,
            distanceMeters = distance,
            avgPace = avgPace,
            bestPace = bestPace,
            calories = calories,
            segments = segments,
        )
    }

    private fun calculateDuration(
        track: Track,
        newSegment: Segment,
    ): Long = track.durationMillis + newSegment.durationMillis

    private fun calculateDistance(
        track: Track,
        newSegment: Segment,
    ): Float =
        when (newSegment) {
            is Segment.Active -> track.distanceMeters + newSegment.distanceMeters
            is Segment.Stationary -> track.distanceMeters
        }

    private fun calculateAvgPace(
        track: Track,
        newSegment: Segment,
    ): Float =
        when (newSegment) {
            is Segment.Active -> (track.avgPace + newSegment.pace) / 2
            is Segment.Stationary -> track.avgPace
        }

    private fun calculateBestPace(
        track: Track,
        newSegment: Segment,
    ): Float =
        when (newSegment) {
            is Segment.Active -> {
                if (track.segments.filterIsInstance<Segment.Active>().isEmpty()) {
                    newSegment.pace
                } else {
                    minOf(track.bestPace, newSegment.pace)
                }
            }

            is Segment.Stationary -> track.bestPace
        }

    private fun calculateCalories(
        track: Track,
        newSegment: Segment,
    ): Float =
        when (newSegment) {
            is Segment.Active -> track.calories + newSegment.calories
            is Segment.Stationary -> track.calories
        }
}
