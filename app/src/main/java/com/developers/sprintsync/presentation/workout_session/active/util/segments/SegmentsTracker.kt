package com.developers.sprintsync.presentation.workout_session.active.util.segments

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Segments
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
