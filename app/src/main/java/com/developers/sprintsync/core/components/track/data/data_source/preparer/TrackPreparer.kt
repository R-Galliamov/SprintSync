package com.developers.sprintsync.core.components.track.data.data_source.preparer

import com.developers.sprintsync.core.components.track.data.model.Track
import javax.inject.Inject

class TrackPreparer
    @Inject
    constructor(
        private val segmentMerger: StationarySegmentMerger,
    ) {
        fun prepareForSave(track: Track): Track {
            val segments = segmentMerger.mergeStationarySegments(track.segments)
            return track.copy(segments = segments)
        }
    }