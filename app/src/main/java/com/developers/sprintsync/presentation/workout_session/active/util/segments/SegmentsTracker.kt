package com.developers.sprintsync.presentation.workout_session.active.util.segments

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.track.model.Segment
import javax.inject.Inject

/**
 * Tracks processed segments and identifies new ones for processing.
 */
class SegmentsTracker @Inject constructor(private val log: AppLogger) {

    private var processedSegmentsForCurrentPath = mutableSetOf<Segment>()

    /*
     * Tracks processed segments and identifies new ones for processing.
     */
    fun getUnprocessedSegments(allCurrentSegments: List<Segment>): List<Segment> {
        if (allCurrentSegments.isEmpty()) return emptyList()
        val newSegments = allCurrentSegments.filter { it !in processedSegmentsForCurrentPath }
        log.d("Identified ${newSegments.size} new segments out of ${allCurrentSegments.size}. Processed before: ${processedSegmentsForCurrentPath.size}")
        return newSegments
    }

    /**
     * Marks a list of segments as processed for the current path rendering..
     */
    fun markSegmentsAsProcessed(segments: List<Segment>) {
        processedSegmentsForCurrentPath.addAll(segments)
        log.d("Marked ${segments.size} segments as processed. Total processed: ${processedSegmentsForCurrentPath.size}")
    }
}
