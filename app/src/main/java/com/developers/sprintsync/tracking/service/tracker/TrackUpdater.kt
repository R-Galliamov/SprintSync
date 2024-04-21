package com.developers.sprintsync.tracking.service.tracker

import com.developers.sprintsync.tracking.model.Segment
import com.developers.sprintsync.tracking.model.Segments
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.service.builder.track.TrackStatCalculator
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
            segment: Segment.ActiveSegment,
        ): Track {
            val durationMillis =
                getUpdatedDurationMillis(track.durationMillis, segment.durationMillis)
            val distanceMeters =
                getUpdatedDistance(track.distanceMeters, segment.distanceMeters)
            val avgPace = getUpdatedAvgPace(track.avgPace, segment.pace)
            val maxPace = getUpdatedMaxPace(track.maxPace, segment.pace)
            val calories = getUpdatedCalories(track.calories, segment.calories)
            val segments = getUpdatedSegments(track.segments, segment)
            val updatedTrack =
                getUpdatedTrack(
                    track,
                    durationMillis,
                    distanceMeters,
                    avgPace,
                    maxPace,
                    calories,
                    segments,
                )
            return updatedTrack
        }

        fun getTrackUpdatedWithSegment(
            track: Track,
            segment: Segment.InactiveSegment,
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
            previousDistanceMeters: Int,
            newDistanceMeters: Int,
        ): Int {
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

        private fun getUpdatedMaxPace(
            previousMaxPace: Float,
            newPace: Float,
        ): Float {
            require(newPace >= 0) { "New pace must be non-negative" }
            return calculator.calculateMaxPace(previousMaxPace, newPace)
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
            distanceMeters: Int,
            avgPace: Float,
            maxPace: Float,
            calories: Int,
            segments: Segments,
        ): Track {
            return track.copy(
                durationMillis = durationMillis,
                distanceMeters = distanceMeters,
                avgPace = avgPace,
                maxPace = maxPace,
                calories = calories,
                segments = segments,
            )
        }

        private fun getUpdatedTrack(
            track: Track,
            durationMillis: Long,
            segments: Segments,
        ): Track {
            return track.copy(durationMillis = durationMillis, segments = segments)
        }
    }
