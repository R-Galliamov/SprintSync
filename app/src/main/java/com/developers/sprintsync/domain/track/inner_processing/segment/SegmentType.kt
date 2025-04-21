package com.developers.sprintsync.domain.track.inner_processing.segment

sealed class SegmentType {
    data object Active : SegmentType()

    data object Stationary : SegmentType()
}