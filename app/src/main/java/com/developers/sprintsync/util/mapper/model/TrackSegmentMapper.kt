package com.developers.sprintsync.util.mapper.model

import com.developers.sprintsync.model.tracking.GeoTimePair
import com.developers.sprintsync.model.tracking.TrackSegment
import com.developers.sprintsync.model.tracking.distanceBetweenInMeters
import com.developers.sprintsync.util.calculator.PaceCalculator
import kotlin.math.roundToInt

fun GeoTimePair.toTrackSegment(endData: GeoTimePair): TrackSegment {
    val duration = endData.timeMillis - this.timeMillis
    val distance = this.location.distanceBetweenInMeters(endData.location).roundToInt()
    val pace = PaceCalculator.getPaceInMinPerKm(duration, distance)
    return TrackSegment(
        startLocation = this.location,
        startTime = this.timeMillis,
        endLocation = endData.location,
        endTime = endData.timeMillis,
        durationMillis = duration,
        distanceMeters = distance,
        pace = pace,
    )
}
