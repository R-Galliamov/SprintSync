package com.developers.sprintsync.domain.track.use_case.service

import com.developers.sprintsync.data.track.service.processing.session.TimedLocation
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.inner_processing.segment.SegmentBuilderFactory

interface SegmentGenerator {
    fun generateSegment(
        segmentId: Long,
        startData: TimedLocation,
        endData: TimedLocation,
        onSuccess: (Segment) -> Unit,
        onFailure: (Throwable) -> Unit,
    )
}

class SegmentGeneratorImpl(
    userWeightKilos: Float,
) : SegmentGenerator {
    private val factory = SegmentBuilderFactory(userWeightKilos)

    override fun generateSegment(
        segmentId: Long,
        startData: TimedLocation,
        endData: TimedLocation,
        onSuccess: (Segment) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        val builder = factory.getBuilder(startData.location, endData.location)
        builder.build(segmentId, startData, endData).fold(
            onSuccess = { segment -> onSuccess(segment) },
            onFailure = { error -> onFailure(error) },
        )
    }
}
