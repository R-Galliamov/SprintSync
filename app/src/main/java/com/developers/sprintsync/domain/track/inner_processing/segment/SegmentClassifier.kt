package com.developers.sprintsync.domain.track.inner_processing.segment

import com.developers.sprintsync.domain.track.model.LocationModel
import com.developers.sprintsync.domain.track.model.distanceBetweenInMeters
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
