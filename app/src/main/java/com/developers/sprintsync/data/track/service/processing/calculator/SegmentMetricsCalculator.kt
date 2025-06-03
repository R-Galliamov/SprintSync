package com.developers.sprintsync.data.track.service.processing.calculator

import com.developers.sprintsync.core.util.time.TimeConverter
import com.developers.sprintsync.data.track.service.processing.calculator.calories.MetCaloriesCalculator
import com.developers.sprintsync.data.track.service.processing.calculator.calories.UserCaloriesCalculator
import com.developers.sprintsync.data.track.service.processing.calculator.speed.SpeedCalculator
import com.developers.sprintsync.data.track.service.processing.calculator.speed.SpeedUnit
import com.developers.sprintsync.domain.track.model.LocationModel
import com.developers.sprintsync.domain.track.model.distanceBetweenInMeters
import javax.inject.Inject

class SegmentMetricsCalculator @Inject constructor(
    private val paceCalculator: PaceCalculator,
) {
    private val caloriesCalculator = UserCaloriesCalculator(MetCaloriesCalculator())

    private val speedCalculator = SpeedCalculator()

    fun calculateDurationInMillis(
        startTimeMillis: Long,
        endTimeMillis: Long,
    ): Long {
        require(startTimeMillis >= 0) { "Start time must be non-negative" }
        require(endTimeMillis >= startTimeMillis) { "End time must be greater than or equal to start time" }
        return endTimeMillis - startTimeMillis
    }

    fun calculateDistanceInMeters(
        firstLocation: LocationModel,
        secondLocation: LocationModel,
    ): Float {
        require(firstLocation != secondLocation) { "Start and end location cannot be the same" }
        return firstLocation.distanceBetweenInMeters(secondLocation)
    }

    fun calculatePaceInMinPerKm(
        durationMillis: Long,
        distanceMeters: Float,
    ): Float = paceCalculator.getPaceInMinPerKm(durationMillis, distanceMeters)

    fun calculateBurnedCalories(
        userWeightKilos: Float,
        distanceMeters: Float,
        durationMillis: Long,
    ): Float {
        val speedInMetersPerMinute =
            speedCalculator.calculateSpeed(durationMillis, distanceMeters, SpeedUnit.METERS_PER_MINUTES)
        val durationHours = TimeConverter.convertFromMillis(durationMillis, TimeConverter.TimeUnit.HOURS)
        return caloriesCalculator.calculateBurnedCalories(userWeightKilos, speedInMetersPerMinute, durationHours)
    }
}
