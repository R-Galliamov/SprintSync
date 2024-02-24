package com.developers.sprintsync.util.mapper.model

import com.developers.sprintsync.model.tracking.GeoTimePair
import com.developers.sprintsync.model.tracking.TrackSegment
import com.developers.sprintsync.model.tracking.distanceBetweenInMeters
import com.developers.sprintsync.util.calculator.CaloriesCalculator
import com.developers.sprintsync.util.calculator.PaceCalculator
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
