package com.developers.sprintsync.presentation.workout_session.active.util.segments

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.track.model.Segment
import javax.inject.Inject

/**
 * Tracks processed segments and identifies new ones for processing.
 */
class SegmentsTracker
@Inject
constructor(private val log: AppLogger) {
    private val processedSegments = mutableListOf<Segment>()

    /**
     * Filters out new segments from the provided list and adds them to the processed list.
     * @param segments The list of [Segment] objects to check.
     * @return A list of new [Segment] objects that were not previously processed.
     */
    fun getNewSegmentsAndAdd(segments: List<Segment>): List<Segment> {
        val newSegments =
            segments.filter { segment ->
                !processedSegments.contains(segment)
            }
        processedSegments.addAll(newSegments)
        log.i("Processed ${newSegments.size} new segments, total processed: ${processedSegments.size}")
        return newSegments
    }
}
