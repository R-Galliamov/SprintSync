package com.developers.sprintsync.tracking.service.builder.track

import javax.inject.Inject

class TrackStatCalculator
    @Inject
    constructor() {
        fun calculateDuration(
            previousDuration: Long,
            newDuration: Long,
        ): Long {
            return previousDuration + newDuration
        }

        fun calculateDistance(
            previousDistanceMeters: Int,
            newDistanceMeters: Int,
        ): Int {
            return previousDistanceMeters + newDistanceMeters
        }

        fun calculateAvgPace(
            previousAvgPace: Float,
            newPace: Float,
        ): Float {
            return (previousAvgPace + newPace) / 2
        }

        fun calculateBestPace(
            previousMaxPace: Float,
            newPace: Float,
        ): Float {
            return minOf(previousMaxPace, newPace)
        }

        fun calculateCalories(
            previousCalories: Int,
            newCalories: Int,
        ): Int {
            return previousCalories + newCalories
        }
    }
