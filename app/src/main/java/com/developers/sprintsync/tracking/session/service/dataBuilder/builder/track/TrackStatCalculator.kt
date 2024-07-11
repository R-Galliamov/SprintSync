package com.developers.sprintsync.tracking.session.service.dataBuilder.builder.track

import javax.inject.Inject

class TrackStatCalculator
    @Inject
    constructor() {
        fun calculateDuration(
            previousDuration: Long,
            newDuration: Long,
        ): Long = previousDuration + newDuration

        fun calculateDistance(
            previousDistanceMeters: Int,
            newDistanceMeters: Int,
        ): Int = previousDistanceMeters + newDistanceMeters

        fun calculateAvgPace(
            previousAvgPace: Float,
            newPace: Float,
        ): Float = (previousAvgPace + newPace) / 2

        fun calculateBestPace(
            previousMaxPace: Float,
            newPace: Float,
        ): Float = minOf(previousMaxPace, newPace)

        fun calculateCalories(
            previousCalories: Int,
            newCalories: Int,
        ): Int = previousCalories + newCalories
    }
