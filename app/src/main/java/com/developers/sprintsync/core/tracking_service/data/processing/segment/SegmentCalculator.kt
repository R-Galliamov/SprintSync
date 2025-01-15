package com.developers.sprintsync.core.tracking_service.data.processing.segment

import com.developers.sprintsync.core.tracking_service.data.model.location.LocationModel
import com.developers.sprintsync.core.tracking_service.data.model.location.distanceBetweenInMeters
import com.developers.sprintsync.core.tracking_service.data.processing.util.calculator.PaceCalculator
import com.developers.sprintsync.core.tracking_service.data.processing.util.calculator.calories.CaloriesCalculatorHelper
import javax.inject.Inject

class SegmentCalculator
    @Inject
    constructor(
        private val caloriesCalculator: CaloriesCalculatorHelper,
        private val paceCalculator: PaceCalculator,
    ) {
        fun calculateDurationInMillis(
            startTimeMillis: Long,
            endTimeMillis: Long,
        ) = endTimeMillis - startTimeMillis

        fun calculateDistanceInMeters(
            firstLocation: LocationModel,
            secondLocation: LocationModel,
        ) = firstLocation.distanceBetweenInMeters(secondLocation)

        fun calculatePaceInMinPerKm(
            durationMillis: Long,
            distanceMeters: Float,
        ): Float = paceCalculator.getPaceInMinPerKm(durationMillis, distanceMeters)

        fun calculateBurnedCalories(
            speedInMeters: Float,
            durationInHours: Float,
        ) = caloriesCalculator.calculateBurnedCalories(speedInMeters, durationInHours)
    }