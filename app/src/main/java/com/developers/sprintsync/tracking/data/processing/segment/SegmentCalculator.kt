package com.developers.sprintsync.tracking.data.processing.segment

import com.developers.sprintsync.core.util.time.TimeConverter
import com.developers.sprintsync.tracking.data.model.LocationModel
import com.developers.sprintsync.tracking.data.model.distanceBetweenInMeters
import com.developers.sprintsync.tracking.data.processing.util.calculator.PaceCalculator
import com.developers.sprintsync.tracking.data.processing.util.calculator.calories.CaloriesCalculatorHelper
import com.developers.sprintsync.tracking.data.processing.util.calculator.speed.SpeedCalculator
import com.developers.sprintsync.tracking.data.processing.util.calculator.speed.SpeedUnit
import javax.inject.Inject

class SegmentCalculator
    @Inject
    constructor(
        private val caloriesCalculator: CaloriesCalculatorHelper,
        private val paceCalculator: PaceCalculator,
        private val speedCalculator: SpeedCalculator,
    ) {
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
            distanceMeters: Float,
            durationMillis: Long,
        ): Float {
            val speedInMetersPerMinute =
                speedCalculator.calculateSpeed(durationMillis, distanceMeters, SpeedUnit.METERS_PER_MINUTES)
            val durationHours = TimeConverter.convertFromMillis(durationMillis, TimeConverter.TimeUnit.HOURS)
            return caloriesCalculator.calculateBurnedCalories(speedInMetersPerMinute, durationHours)
        }
    }
