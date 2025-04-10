package com.developers.sprintsync.domain.tracking_service.internal.data_processing.segment

import com.developers.sprintsync.domain.tracking_service.model.LocationModel
import com.developers.sprintsync.domain.tracking_service.model.distanceBetweenInMeters
import javax.inject.Inject

class SegmentClassifier
    @Inject
    constructor() {
        fun classifySegment(
            startLocation: LocationModel,
            endLocation: LocationModel,
        ): SegmentType {
            val locationHasChanged = startLocation.distanceBetweenInMeters(endLocation) >= DISTANCE_THRESHOLD_METERS
            return when (locationHasChanged) {
                true -> SegmentType.Active
                false -> SegmentType.Stationary
            }
        }

        companion object {
            private const val DISTANCE_THRESHOLD_METERS = 2
        }
    }