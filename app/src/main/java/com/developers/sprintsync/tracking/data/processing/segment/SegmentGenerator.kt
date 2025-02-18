package com.developers.sprintsync.tracking.data.processing.segment

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.tracking.data.model.TimedLocation
import javax.inject.Inject

class SegmentGenerator
    @Inject
    constructor(
        private val factory: SegmentBuilderFactory,
    ) {
        fun buildSegment(
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