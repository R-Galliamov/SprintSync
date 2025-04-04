package com.developers.sprintsync.domain.track.validation.track

import com.developers.sprintsync.domain.track.validation.ValidationError

sealed class TrackValidationError(
    override val message: String,
) : ValidationError {
    data object InvalidTimestamp : TrackValidationError("Invalid timestamp")

    data object DurationTooShort : TrackValidationError("Duration must be greater than zero")

    data object DistanceTooShort : TrackValidationError("Distance must be greater than zero")

    data object AvgPaceInvalid : TrackValidationError("Average pace must be positive")

    data object BestPaceInvalid : TrackValidationError("Best pace must be positive")

    data object CaloriesNegative : TrackValidationError("Calories burned cannot be negative")

    data object TooFewSegments : TrackValidationError("Track must contain at least two segments")
}
