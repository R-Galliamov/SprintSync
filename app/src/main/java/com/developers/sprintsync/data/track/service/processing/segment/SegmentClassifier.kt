package com.developers.sprintsync.data.track.service.processing.segment

import com.developers.sprintsync.data.track.service.processing.calculator.DistanceCalculator
import com.developers.sprintsync.domain.track.model.LocationModel
import javax.inject.Inject

class SegmentClassifier @Inject constructor(
    private val distanceCalculator: DistanceCalculator,
) {

    private var stationaryCounter = 0
    private var lastType: SegmentType = SegmentType.Active

    /**
     * Classifies a segment between two locations as Active or Stationary.
     *
     * - If the distance change exceeds the threshold → Active.
     * - If the distance is below threshold:
     *   - a few consecutive stationary points are treated as GPS noise → keep last state
     *   - enough consecutive stationary points confirm a real stop → Stationary
     */
    fun classifySegment(
        startLocation: LocationModel,
        endLocation: LocationModel,
    ): SegmentType {
        val locationHasChanged =
            distanceCalculator.distanceBetweenInMeters(startLocation, endLocation) >= DISTANCE_THRESHOLD_METERS

        return if (locationHasChanged) {
            // movement detected → reset stationary counter
            stationaryCounter = 0
            lastType = SegmentType.Active
            SegmentType.Active
        } else {
            // no significant movement
            stationaryCounter++

            // short stationary streak → consider noise, keep last state
            if (stationaryCounter < STATIONARY_CONFIRMATION_COUNT) {
                lastType
            } else {
                // confirmed stop
                lastType = SegmentType.Stationary
                SegmentType.Stationary
            }
        }
    }

    companion object {
        private const val DISTANCE_THRESHOLD_METERS = 2
        private const val STATIONARY_CONFIRMATION_COUNT = 3 // consecutive points required to confirm stop
    }
}
