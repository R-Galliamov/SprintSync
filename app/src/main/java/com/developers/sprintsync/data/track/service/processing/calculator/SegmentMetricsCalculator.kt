package com.developers.sprintsync.data.track.service.processing.calculator

import com.developers.sprintsync.core.util.time.TimeConverter
import com.developers.sprintsync.data.track.service.processing.calculator.calories.CaloriesCalculator
import com.developers.sprintsync.data.track.service.processing.calculator.speed.SpeedCalculator
import com.developers.sprintsync.data.track.service.processing.calculator.speed.SpeedUnit
import com.developers.sprintsync.domain.track.model.LocationModel
import com.developers.sprintsync.domain.track.model.distanceBetweenInMeters
import javax.inject.Inject

/**
 * Calculates metrics for a track segment, such as duration, distance, pace, and calories burned.
 */
class SegmentMetricsCalculator @Inject constructor(
    private val paceCalculator: PaceCalculator,
) {
    private val caloriesCalculator = CaloriesCalculator()

    private val speedCalculator = SpeedCalculator()

    /**
     * Calculates the duration of a segment in milliseconds.
     * @param startTimeMillis Start time of the segment in milliseconds.
     * @param endTimeMillis End time of the segment in milliseconds.
     * @return Duration in milliseconds.
     * @throws IllegalArgumentException if startTimeMillis is negative or endTimeMillis is less than startTimeMillis.
     */
    fun calculateDurationMillis(
        startTimeMillis: Long,
        endTimeMillis: Long,
    ): Long {
        require(startTimeMillis >= 0) { "Start time must be non-negative" }
        require(endTimeMillis >= startTimeMillis) { "End time must be greater than or equal to start time" }
        return endTimeMillis - startTimeMillis
    }

    /**
     * Calculates the distance between two locations in meters.
     * @param firstLocation Starting location of the segment.
     * @param secondLocation Ending location of the segment.
     * @return Distance in meters.
     * @throws IllegalArgumentException if firstLocation and secondLocation are the same.
     */
    fun calculateDistanceMeters(
        firstLocation: LocationModel,
        secondLocation: LocationModel,
    ): Float {
        require(firstLocation != secondLocation) { "Start and end location cannot be the same" }
        return firstLocation.distanceBetweenInMeters(secondLocation)
    }

    /**
     * Calculates the pace in minutes per kilometer.
     * @param durationMillis Duration of the segment in milliseconds.
     * @param distanceMeters Distance of the segment in meters.
     * @return Pace in minutes per kilometer.
     */
    fun calculatePaceMPKm(
        durationMillis: Long,
        distanceMeters: Float,
    ): Float = paceCalculator.getPaceInMinPerKm(durationMillis, distanceMeters)

    /**
     * Calculates calories burned during a segment based on user weight, distance, and duration.
     * @param weightKg User's weight in kilograms.
     * @param distanceMeters Distance of the segment in meters.
     * @param durationMillis Duration of the segment in milliseconds.
     * @return Calories burned.
     */
    fun calculateCalories(
        weightKg: Float,
        distanceMeters: Float,
        durationMillis: Long,
    ): Float {
        val speedInMetersPerMinute =
            speedCalculator.calculateSpeed(durationMillis, distanceMeters, SpeedUnit.METERS_PER_MINUTES)
        val durationHours = TimeConverter.convertFromMillis(durationMillis, TimeConverter.TimeUnit.HOURS)
        return caloriesCalculator.calculateCalories(weightKg, speedInMetersPerMinute, durationHours)
    }
}
