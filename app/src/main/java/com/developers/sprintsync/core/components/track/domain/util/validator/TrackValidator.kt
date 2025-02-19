package com.developers.sprintsync.core.components.track.domain.util.validator

import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.util.validation.ValidationException
import com.developers.sprintsync.core.util.validation.Validator

object TrackValidator : Validator<Track> {
    private const val MIN_TIMESTAMP = 0L
    private const val MIN_DURATION = 0L
    private const val MIN_DISTANCE = 0
    private const val MIN_PACE = 0f
    private const val MIN_CALORIES = 0

    override fun validateOrThrow(data: Track): Track {
        val errors = mutableListOf<TrackValidationError>()

        if (data.timestamp < MIN_TIMESTAMP) errors.add(TrackValidationError.InvalidTimestamp)
        if (data.durationMillis <= MIN_DURATION) errors.add(TrackValidationError.DurationTooShort)
        if (data.distanceMeters <= MIN_DISTANCE) errors.add(TrackValidationError.DistanceTooShort)
        if (data.avgPace <= MIN_PACE) errors.add(TrackValidationError.AvgPaceInvalid)
        if (data.bestPace <= MIN_PACE) errors.add(TrackValidationError.BestPaceInvalid)
        if (data.calories < MIN_CALORIES) errors.add(TrackValidationError.CaloriesNegative)
        if (data.segments.isEmpty()) {
            errors.add(TrackValidationError.NoSegments)
        }

        if (errors.isNotEmpty()) throw ValidationException(data, errors)

        return data
    }
}
