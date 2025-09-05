package com.developers.sprintsync.data.track.service.processing.track

import com.developers.sprintsync.data.track.service.processing.calculator.pace.PaceCalculator
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track
import javax.inject.Inject

class TrackCalculator @Inject constructor(
    private val paceCalculator: PaceCalculator,
) {
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
    ): Float = track.distanceMeters + newSegment.distanceMeters

    private fun calculateAvgPace(
        track: Track,
        newSegment: Segment,
    ): Float {
        val distance = track.distanceMeters + newSegment.distanceMeters
        val duration = track.durationMillis + newSegment.durationMillis
        return paceCalculator.getPaceInMinPerKm(duration, distance)
    }

    private fun calculateBestPace(
        track: Track,
        newSegment: Segment,
    ): Float? {
        val segmentPace = newSegment.pace
        return when {
            track.bestPace == null -> segmentPace
            segmentPace == null -> track.bestPace
            else -> minOf(track.bestPace, segmentPace)
        }
    }


    private fun calculateCalories(
        track: Track,
        newSegment: Segment,
    ): Float = track.calories + newSegment.calories
}