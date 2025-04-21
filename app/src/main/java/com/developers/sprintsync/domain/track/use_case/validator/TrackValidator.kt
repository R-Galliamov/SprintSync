package com.developers.sprintsync.domain.track.use_case.validator

import com.developers.sprintsync.domain.track.model.Track

object TrackValidator {
    private const val MIN_TIMESTAMP = 0L
    private const val MIN_DURATION = 0L
    private const val MIN_DISTANCE = 0
    private const val MIN_PACE = 0f
    private const val MIN_CALORIES = 0
    private const val MIN_SEGMENTS = 2

    /**
     * Validates a [Track] and throws a [TrackValidationException] if any validation fails.
     *
     * @param data The track to validate.
     * @return The validated track if all checks pass.
     * @throws TrackValidationException If any validation rule is violated.
     */
    fun validateOrThrow(data: Track): Track {
        if (data.timestamp < MIN_TIMESTAMP) throw TrackValidationException.InvalidTimestamp()
        if (data.durationMillis <= MIN_DURATION) throw TrackValidationException.DurationTooShort()
        if (data.distanceMeters <= MIN_DISTANCE) throw TrackValidationException.DistanceTooShort()
        if (data.avgPace <= MIN_PACE) throw TrackValidationException.AvgPaceInvalid()
        if (data.bestPace <= MIN_PACE) throw TrackValidationException.BestPaceInvalid()
        if (data.calories < MIN_CALORIES) throw TrackValidationException.CaloriesNegative()
        if (data.segments.size < MIN_SEGMENTS) throw TrackValidationException.TooFewSegments()
        return data
    }
}

sealed class TrackValidationException(
    override val message: String,
) : Exception(message) {
    class InvalidTimestamp : TrackValidationException("Invalid timestamp")

    class DurationTooShort : TrackValidationException("Duration must be greater than zero")

    class DistanceTooShort : TrackValidationException("Distance must be greater than zero")

    class AvgPaceInvalid : TrackValidationException("Average pace must be positive")

    class BestPaceInvalid : TrackValidationException("Best pace must be positive")

    class CaloriesNegative : TrackValidationException("Calories burned cannot be negative")

    class TooFewSegments : TrackValidationException("Track must contain at least two segments")
}