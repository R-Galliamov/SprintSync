package com.developers.sprintsync.tracking.data.processing.track

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Segments
import com.developers.sprintsync.core.components.track.data.model.Track
import javax.inject.Inject

// TODO add logic for handling overflows
// TODO add logic for handling track values errors
class TrackUpdater
    @Inject
    constructor(
        private val calculator: TrackStatCalculator,
    ) {
        fun getTrackUpdatedWithSegment(
            track: Track,
            segment: Segment.Active,
        ): Track {
            val durationMillis =
                getUpdatedDurationMillis(track.durationMillis, segment.durationMillis)
            val distanceMeters =
                getUpdatedDistance(track.distanceMeters, segment.distanceMeters)
            val avgPace = getUpdatedAvgPace(track.avgPace, segment.pace)
            val bestPace = getUpdatedBestPace(track.bestPace, segment.pace)
            val calories = getUpdatedCalories(track.calories, segment.calories)
            val segments = getUpdatedSegments(track.segments, segment)
            val updatedTrack =
                getUpdatedTrack(
                    track,
                    durationMillis,
                    distanceMeters,
                    avgPace,
                    bestPace,
                    calories,
                    segments,
                )
            return updatedTrack
        }

        fun getTrackUpdatedWithSegment(
            track: Track,
            segment: Segment.Stationary,
        ): Track {
            val durationMillis =
                calculator.calculateDuration(track.durationMillis, segment.durationMillis)
            val segments = getUpdatedSegments(track.segments, segment)
            val updatedTrack = getUpdatedTrack(track, durationMillis, segments)
            return updatedTrack
        }

        private fun getUpdatedDurationMillis(
            previousDuration: Long,
            newDuration: Long,
        ): Long {
            require(newDuration >= 0) { "New duration must be non-negative" }
            return calculator.calculateDuration(previousDuration, newDuration)
        }

        private fun getUpdatedDistance(
            previousDistanceMeters: Float,
            newDistanceMeters: Float,
        ): Float {
            require(newDistanceMeters >= 0) { "New distance must be non-negative" }
            return calculator.calculateDistance(previousDistanceMeters, newDistanceMeters)
        }

        private fun getUpdatedAvgPace(
            previousAvgPace: Float,
            newPace: Float,
        ): Float {
            require(newPace >= 0) { "New pace must be non-negative" }
            return calculator.calculateAvgPace(previousAvgPace, newPace)
        }

        private fun getUpdatedBestPace(
            previousMaxPace: Float,
            newPace: Float,
        ): Float {
            require(newPace >= 0) { "New pace must be non-negative" }
            return if (previousMaxPace == 0f) newPace else calculator.calculateBestPace(previousMaxPace, newPace)
        }

        private fun getUpdatedCalories(
            previousCalories: Int,
            newCalories: Int,
        ): Int {
            require(newCalories >= 0) { "New calories must be non-negative" }
            return calculator.calculateCalories(previousCalories, newCalories)
        }

        private fun getUpdatedSegments(
            segments: Segments,
            newSegment: Segment,
        ): Segments {
            val newSegments = segments.toMutableList() + newSegment
            return newSegments
        }

        private fun getUpdatedTrack(
            track: Track,
            durationMillis: Long,
            distanceMeters: Float,
            avgPace: Float,
            maxPace: Float,
            calories: Int,
            segments: Segments,
        ): Track =
            track.copy(
                durationMillis = durationMillis,
                distanceMeters = distanceMeters,
                avgPace = avgPace,
                bestPace = maxPace,
                calories = calories,
                segments = segments,
            )

        private fun getUpdatedTrack(
            track: Track,
            durationMillis: Long,
            segments: Segments,
        ): Track = track.copy(durationMillis = durationMillis, segments = segments)
    }
