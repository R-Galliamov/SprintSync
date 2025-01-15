package com.developers.sprintsync.core.tracking_service.data.processing.segment

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.tracking_service.data.model.location.GeoTimePoint

sealed class SegmentGeneratorState {
    data object Uninitialized : SegmentGeneratorState()

    open class Initialized(
        val lastDataPoint: GeoTimePoint,
    ) : SegmentGeneratorState()

    class GeneratingSegments(
        lastDataPoint: GeoTimePoint,
        val segment: Segment,
        val segmentId: Long,
    ) : Initialized(lastDataPoint)
}