package com.developers.sprintsync.domain.tracking_service.internal.data_processing.segment

sealed class SegmentType {
    data object Active : SegmentType()

    data object Stationary : SegmentType()
}