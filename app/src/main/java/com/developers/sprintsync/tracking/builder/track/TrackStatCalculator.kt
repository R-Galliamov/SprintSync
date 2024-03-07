package com.developers.sprintsync.tracking.builder.track

import javax.inject.Inject

class TrackStatCalculator
    @Inject
    constructor() {
        fun calculateDuration(
            previousDuration: Long,
            newDuration: Long,
        ): Long {
            check(previousDuration >= 0) { "Previous duration must be non-negative" }
            check(newDuration >= 0) { "New duration must be non-negative" }
            return previousDuration + newDuration
        }

        fun calculateDistance(
            previousDistanceMeters: Int,
            newDistanceMeters: Int,
        ): Int {
            check(previousDistanceMeters >= 0) { "Previous distance must be non-negative" }
            check(newDistanceMeters >= 0) { "New distance must be non-negative" }
            return previousDistanceMeters + newDistanceMeters
        }

        fun calculateAvgPace(
            previousAvgPace: Float,
            newPace: Float,
        ): Float {
            check(previousAvgPace >= 0) { "Previous average pace must be non-negative" }
            check(newPace >= 0) { "New average pace must be non-negative" }
            return (previousAvgPace + newPace) / 2
        }

        fun calculateMaxPace(
            previousMaxPace: Float,
            newPace: Float,
        ): Float {
            check(previousMaxPace >= 0) { "Previous max pace must be non-negative" }
            check(newPace >= 0) { "New max pace must be non-negative" }
            return maxOf(previousMaxPace, newPace)
        }

        fun calculateCalories(
            previousCalories: Int,
            newCalories: Int,
        ): Int {
            check(previousCalories >= 0) { "Previous calories must be non-negative" }
            check(newCalories >= 0) { "New calories must be non-negative" }
            return previousCalories + newCalories
        }
    }
