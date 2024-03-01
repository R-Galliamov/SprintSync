package com.developers.sprintsync.tracking.mapper.model

import com.developers.sprintsync.tracking.model.GeoTimePair
import com.developers.sprintsync.tracking.model.TrackSegment
import com.developers.sprintsync.tracking.model.distanceBetweenInMeters
import com.developers.sprintsync.tracking.util.calculator.CaloriesCalculator
import com.developers.sprintsync.tracking.util.calculator.PaceCalculator
import javax.inject.Inject
import kotlin.math.roundToInt

class TrackSegmentMapper
    @Inject
    constructor(
        private val caloriesCalculator: CaloriesCalculator,
    ) {
        fun buildTrackSegment(
            startData: GeoTimePair,
            endData: GeoTimePair,
        ): TrackSegment {
            val duration = endData.timeMillis - startData.timeMillis
            val distance = startData.location.distanceBetweenInMeters(endData.location).roundToInt()
            val pace = PaceCalculator.getPaceInMinPerKm(duration, distance)
            val burnedKCalories = caloriesCalculator.getBurnedKiloCalories(distance)
            return TrackSegment(
                startLocation = startData.location,
                startTime = startData.timeMillis,
                endLocation = endData.location,
                endTime = endData.timeMillis,
                durationMillis = duration,
                distanceMeters = distance,
                pace = pace,
                burnedKCalories = burnedKCalories,
            )
        }
    }
