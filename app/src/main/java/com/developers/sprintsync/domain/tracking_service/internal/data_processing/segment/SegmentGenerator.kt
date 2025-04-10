package com.developers.sprintsync.domain.tracking_service.internal.data_processing.segment

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.tracking_service.model.TimedLocation
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