package com.developers.sprintsync.updater

import com.developers.sprintsync.tracking.generator.segment.SegmentGenerator
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

        // todo: Try reactive approach here and in SegmentsGenerator
        fun getTrack(
            locationModel: LocationModel,
            timeMillis: Long,
        ): Track {
            val segment = segmentGenerator.nextSegmentOrNull(locationModel, timeMillis)
            segment?.let { updateCurrent(segment) }
            return currentTrack
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
            val segments = track.segments.toMutableList().apply { add(newSegment) }
            return track.copy(
                durationMillis = calculator.calculateDuration(track, newSegment),
                distanceMeters = calculator.calculateDistance(track, newSegment),
                avgPace = calculator.calculateAvgPace(track, newSegment),
                maxPace = calculator.calculateMaxPace(track, newSegment),
                calories = calculator.calculateCalories(track, newSegment),
                segments = segments,
            )
        }

        fun onPause() {
            segmentGenerator.reset()
        }
    }
