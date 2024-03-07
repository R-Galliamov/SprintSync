package com.developers.sprintsync.tracking.builder.track

import com.developers.sprintsync.tracking.builder.segment.SegmentGenerator
import com.developers.sprintsync.tracking.model.LocationModel
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.model.TrackSegment
import javax.inject.Inject

class TrackUpdater
    @Inject
    constructor(
        private val creator: TrackCreator,
        private val calculator: TrackStatCalculator,
        private val segmentGenerator: SegmentGenerator,
    ) {
        private var currentTrack = Track.EMPTY_TRACK_DATA

        fun getTrack(
            locationModel: LocationModel,
            timeMillis: Long,
        ): Track {
            val segment = segmentGenerator.nextSegmentOrNull(locationModel, timeMillis)
            segment?.let { updateCurrent(segment) }
            return currentTrack
        }

        fun onPause() {
            segmentGenerator.reset()
        }

        private fun updateCurrent(newSegment: TrackSegment) {
            currentTrack =
                if (currentTrack == Track.EMPTY_TRACK_DATA) {
                    creator.createTrackData(newSegment)
                } else {
                    getUpdated(currentTrack, newSegment)
                }
        }

        private fun getUpdated(
            track: Track,
            newSegment: TrackSegment,
        ): Track {
            val updatedDuration =
                calculateUpdatedDuration(track.durationMillis, newSegment.durationMillis)
            val updatedDistance =
                calculateUpdatedDistance(track.distanceMeters, newSegment.distanceMeters)
            val updatedAvgPace = calculateUpdatedAvgPace(track.avgPace, newSegment.pace)
            val updatedMaxPace = calculateUpdatedMaxPace(track.maxPace, newSegment.pace)
            val updatedCalories = calculateUpdatedCalories(track.calories, newSegment.burnedKCalories)

            val segments = track.segments.toMutableList().apply { add(newSegment) }

            return track.copy(
                durationMillis = updatedDuration,
                distanceMeters = updatedDistance,
                avgPace = updatedAvgPace,
                maxPace = updatedMaxPace,
                calories = updatedCalories,
                segments = segments,
            )
        }

        private fun calculateUpdatedDuration(
            previousDuration: Long,
            newDuration: Long,
        ): Long {
            return calculator.calculateDuration(previousDuration, newDuration)
        }

        private fun calculateUpdatedDistance(
            previousDistanceMeters: Int,
            newDistanceMeters: Int,
        ): Int {
            return calculator.calculateDistance(previousDistanceMeters, newDistanceMeters)
        }

        private fun calculateUpdatedAvgPace(
            previousAvgPace: Float,
            newPace: Float,
        ): Float {
            return calculator.calculateAvgPace(previousAvgPace, newPace)
        }

        private fun calculateUpdatedMaxPace(
            previousMaxPace: Float,
            newPace: Float,
        ): Float {
            return calculator.calculateMaxPace(previousMaxPace, newPace)
        }

        private fun calculateUpdatedCalories(
            previousCalories: Int,
            newCalories: Int,
        ): Int {
            return calculator.calculateCalories(previousCalories, newCalories)
        }
    }
