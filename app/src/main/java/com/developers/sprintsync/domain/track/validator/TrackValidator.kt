package com.developers.sprintsync.domain.track.validator

import com.developers.sprintsync.domain.track.model.Track

data class TrackValidationPolicy(
    val minTimestamp: Long,
    val minDuration: Long,
    val minDistance: Int,
    val minPace: Float,
    val minCalories: Int,
    val minSegments: Int,
)

object TrackLimits {
    const val MIN_TIMESTAMP: Long = 0L
    const val MIN_DURATION: Long = 0L
    const val MIN_DISTANCE: Int = 0
    const val MIN_PACE: Float = 0f
    const val MIN_CALORIES: Int = 0
    const val MIN_SEGMENTS: Int = 2
}


class TrackValidator(private val policy: TrackValidationPolicy) {

    fun validate(track: Track): Set<TrackErrors> {
        val errors = mutableSetOf<TrackErrors>()

        if (track.timestamp < policy.minTimestamp) errors += TrackErrors.InvalidTimestamp
        if (track.durationMillis <= policy.minDuration) errors += TrackErrors.DurationTooShort
        if (track.distanceMeters <= policy.minDistance) errors += TrackErrors.DistanceTooShort
        if (track.avgPace == null) errors += TrackErrors.AvgPaceInvalid else if (track.avgPace <= policy.minPace) errors += TrackErrors.AvgPaceInvalid
        if (track.bestPace == null) errors += TrackErrors.BestPaceInvalid else if (track.bestPace <= policy.minPace) errors += TrackErrors.BestPaceInvalid
        if (track.calories < policy.minCalories) errors += TrackErrors.CaloriesNegative
        if (track.segments.size < policy.minSegments) errors += TrackErrors.TooFewSegments

        return errors
    }
}

enum class TrackErrors {
    InvalidTimestamp,
    DurationTooShort,
    DistanceTooShort,
    AvgPaceInvalid,
    BestPaceInvalid,
    CaloriesNegative,
    TooFewSegments
}