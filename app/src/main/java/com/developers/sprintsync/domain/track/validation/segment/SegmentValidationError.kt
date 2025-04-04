package com.developers.sprintsync.domain.track.validation.segment

import com.developers.sprintsync.domain.track.validation.ValidationError

sealed class SegmentValidationError(
    override val message: String,
) : ValidationError {
    data object DurationTooShort : SegmentValidationError("Duration must be greater than zero")

    data object DistanceTooShort : SegmentValidationError("Distance must be greater than zero")

    data object PaceTooSlow : SegmentValidationError("Pace is too slow")

    data object PaceTooFast : SegmentValidationError("Pace is too fast")

    data object CaloriesNegative : SegmentValidationError("Calories burned cannot be negative")

}