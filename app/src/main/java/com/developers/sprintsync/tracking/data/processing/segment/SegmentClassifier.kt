package com.developers.sprintsync.tracking.data.processing.segment

import com.developers.sprintsync.tracking.data.model.LocationModel
import com.developers.sprintsync.tracking.data.model.distanceBetweenInMeters
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