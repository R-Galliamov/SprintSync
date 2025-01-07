package com.developers.sprintsync.core.tracking_service.data.processing.segment

import com.developers.sprintsync.core.tracking_service.data.model.location.GeoTimePoint
import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.tracking_service.data.model.location.distanceBetweenInMeters
import com.developers.sprintsync.core.tracking_service.data.processing.util.calculator.CaloriesCalculator
import com.developers.sprintsync.core.tracking_service.data.processing.util.calculator.PaceCalculator
import javax.inject.Inject
import kotlin.math.roundToInt

class SegmentBuilder
    @Inject
    constructor(
        private val caloriesCalculator: CaloriesCalculator,
    ) {
        fun buildActiveTrackSegment(
            id: Long,
            startData: GeoTimePoint,
            endData: GeoTimePoint,
        ): Segment {
            val duration = endData.timeMillis - startData.timeMillis
            val distance = startData.location.distanceBetweenInMeters(endData.location).roundToInt()
            val pace = PaceCalculator.getPace(duration, distance)
            val burnedKCalories = caloriesCalculator.getBurnedCalories(distance)
            return Segment.ActiveSegment(
                id = id,
                startLocation = startData.location,
                startTime = startData.timeMillis,
                endLocation = endData.location,
                endTime = endData.timeMillis,
                durationMillis = duration,
                distanceMeters = distance,
                pace = pace,
                calories = burnedKCalories,
            )
        }

        fun buildInactiveSegment(
            id: Long,
            startData: GeoTimePoint,
            endTimeMillis: Long,
        ): Segment {
            val duration = endTimeMillis - startData.timeMillis
            return Segment.InactiveSegment(
                id = id,
                location = startData.location,
                startTime = startData.timeMillis,
                endTime = endTimeMillis,
                durationMillis = duration,
            )
        }
    }
