package com.developers.sprintsync.data.track.service.processing.segment

sealed class SegmentType {
    data object Active : SegmentType()

    data object Stationary : SegmentType()
}