package com.developers.sprintsync.domain.tracking_service.internal.data_processing.segment

import com.developers.sprintsync.domain.tracking_service.model.TimedLocation

sealed class SegmentGeneratingState {
    data object Uninitialized : SegmentGeneratingState()

    open class Initialized(
        val lastData: TimedLocation,
    ) : SegmentGeneratingState()

    class GeneratingSegments(
        lastData: TimedLocation,
    ) : Initialized(lastData)
}