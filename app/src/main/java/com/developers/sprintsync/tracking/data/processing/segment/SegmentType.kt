package com.developers.sprintsync.tracking.data.processing.segment

sealed class SegmentType {
    data object Active : SegmentType()

    data object Stationary : SegmentType()
}