package com.developers.sprintsync.tracking.data.processing.segment

import com.developers.sprintsync.tracking.data.model.TimedLocation

sealed class SegmentGeneratingState {
    data object Uninitialized : SegmentGeneratingState()

    open class Initialized(
        val lastData: TimedLocation,
    ) : SegmentGeneratingState()

    class GeneratingSegments(
        lastData: TimedLocation,
    ) : Initialized(lastData)
}