package com.developers.sprintsync.tracking.session.service.dataBuilder.builder.track

import com.developers.sprintsync.tracking.session.model.track.LocationModel
import com.developers.sprintsync.tracking.session.model.track.Segment
import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.tracking.session.service.dataBuilder.builder.segment.SegmentGenerator
import javax.inject.Inject

class TrackBuilder
    @Inject
    constructor(
        private val initializer: TrackInitializer,
        private val segmentGenerator: SegmentGenerator,
        private val trackUpdater: TrackUpdater,
    ) {
        private var track: Track = Track.EMPTY_TRACK_DATA
        private val lock = Any()

        // when user is moving
        fun addActiveDataPoint(
            location: LocationModel,
            timeMillis: Long,
        ) {
            synchronized(lock) {
                segmentGenerator.addActiveDataPoint(location, timeMillis)
                val segment = segmentGenerator.getCurrentSegment()
                segment?.let { updateTrack(it) }
            }
        }

        // when user isn't  moving, but tracker is working
        fun addInactiveDataPoint(endPauseTimeMillis: Long) {
            synchronized(lock) {
                segmentGenerator.addInactiveDataPoint(
                    endPauseTimeMillis,
                )
                val segment = segmentGenerator.getCurrentSegment()
                segment?.let { updateTrack(it) }
            }
        }

        fun clearLastDataPoint() {
            synchronized(lock) {
                segmentGenerator.clearLastData()
            }
        }

        fun reset() {
            synchronized(lock) {
                track = Track.EMPTY_TRACK_DATA
            }
            clearLastDataPoint()
        }

        fun buildTrack(): Track {
            synchronized(lock) {
                return track
            }
        }

        private fun updateTrack(segment: Segment) {
            synchronized(lock) {
                if (!isTrackInitialized()) {
                    initializeTrack(segment)
                    return
                }
                val updatedTrack: Track =
                    when (segment) {
                        is Segment.ActiveSegment -> {
                            trackUpdater.getTrackUpdatedWithSegment(track, segment)
                        }

                        is Segment.InactiveSegment -> {
                            trackUpdater.getTrackUpdatedWithSegment(track, segment)
                        }
                    }
                setTrack(updatedTrack)
            }
        }

        private fun initializeTrack(initialSegment: Segment) {
            synchronized(lock) {
                val track = initializer.initializeTrack(initialSegment)
                setTrack(track)
            }
        }

        private fun isTrackInitialized(): Boolean {
            synchronized(lock) {
                return track != Track.EMPTY_TRACK_DATA
            }
        }

        private fun setTrack(track: Track) {
            synchronized(lock) {
                this.track = track
            }
        }
    }
