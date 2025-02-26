package com.developers.sprintsync.tracking_session.presentation.tracking.util.segments

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Segments
import javax.inject.Inject

class SegmentsTracker
    @Inject
    constructor() {
        private val processedSegments = mutableListOf<Segment>()

        fun getNewSegmentsAndAdd(segments: Segments): Segments {
            val newSegments =
                segments.filter { segment ->
                    !processedSegments.contains(segment)
                }
            processedSegments.addAll(newSegments)
            return newSegments
        }
    }
