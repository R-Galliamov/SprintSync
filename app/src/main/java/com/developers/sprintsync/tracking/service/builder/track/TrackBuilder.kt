package com.developers.sprintsync.tracking.service.builder.track

import com.developers.sprintsync.tracking.model.LocationModel
import com.developers.sprintsync.tracking.model.Segment
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.service.builder.segment.SegmentBuilder
import com.developers.sprintsync.tracking.service.tracker.TrackUpdater
import javax.inject.Inject

class TrackBuilder
    @Inject
    constructor(
        private val initializer: TrackInitializer,
        private val segmentBuilder: SegmentBuilder,
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
                segmentBuilder.addActiveDataPoint(location, timeMillis)
                val segment = segmentBuilder.getCurrentSegment()
                segment?.let { updateTrack(it) }
            }
        }

        // when user isn't  moving, but tracker is working
        fun addInactiveDataPoint(endPauseTimeMillis: Long) {
            synchronized(lock) {
                segmentBuilder.addInactiveDataPoint(
                    endPauseTimeMillis,
                )
                val segment = segmentBuilder.getCurrentSegment()
                segment?.let { updateTrack(it) }
            }
        }

        fun clearLastDataPoint() {
            synchronized(lock) {
                segmentBuilder.clearLastDataPoint()
            }
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
