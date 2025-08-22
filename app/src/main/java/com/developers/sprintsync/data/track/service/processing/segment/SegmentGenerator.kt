package com.developers.sprintsync.data.track.service.processing.segment

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.processing.session.TimedLocation
import com.developers.sprintsync.domain.track.model.Segment
import javax.inject.Inject

/**
 * Generates segments from timed location data.
 */
interface SegmentGenerator {
    /**
     * Generates a segment from start and end location data.
     * @param segmentId Unique identifier for the segment.
     * @param startData Starting [TimedLocation] of the segment.
     * @param endData Ending [TimedLocation] of the segment.
     * @param onSuccess Callback invoked with the generated [Segment] on success.
     * @param onFailure Callback invoked with a [Throwable] on failure.
     */
    fun generateSegment(
        segmentId: Long,
        startData: TimedLocation,
        endData: TimedLocation,
        onSuccess: (Segment) -> Unit,
        onFailure: (Throwable) -> Unit,
    )
}

/**
 * Implementation of [SegmentGenerator] using a segment builder factory.
 */
class SegmentGeneratorImpl @Inject constructor(
    private val factory: SegmentBuilderFactory,
    private val log: AppLogger,
) : SegmentGenerator {

    override fun generateSegment(
        segmentId: Long,
        startData: TimedLocation,
        endData: TimedLocation,
        onSuccess: (Segment) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        val builder = factory.getBuilder(startData.location, endData.location)
        builder.build(segmentId, startData, endData).fold(
            onSuccess = { segment ->
                onSuccess(segment)
                log.i("Segment generated: $segment")
            },
            onFailure = { e ->
                log.e("Failed to generate segment id=$segmentId: ${e.message}", e)
                onFailure(e)
            },
        )
    }
}
