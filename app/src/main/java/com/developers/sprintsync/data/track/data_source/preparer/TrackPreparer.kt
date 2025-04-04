package com.developers.sprintsync.data.track.data_source.preparer

import com.developers.sprintsync.domain.track.model.Track
import javax.inject.Inject

class TrackPreparer // TODO Move to domain
    @Inject
    constructor(
        private val segmentMerger: StationarySegmentMerger,
    ) {
        fun prepareForSave(track: Track): Track {
            val segments = segmentMerger.mergeStationarySegments(track.segments)
            return track.copy(segments = segments)
        }
    }