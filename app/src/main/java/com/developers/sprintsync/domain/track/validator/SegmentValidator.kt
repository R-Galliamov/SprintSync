package com.developers.sprintsync.domain.track.validator

import com.developers.sprintsync.domain.track.model.Segment

object SegmentValidationRules {
    const val MIN_DISTANCE_METERS = 0
    const val MIN_DURATION_MILLIS = 0
    const val MIN_PACE_MIN_PER_KM = 2
    const val MAX_PACE_MIN_PER_KM = 20
    const val MIN_CALORIES = 0
}

class SegmentValidator : Validator<Segment> {

    /**
     * Validates the given [Segment] data.
     *
     * @param data The [Segment] to validate.
     * @return A [Result] containing the validated [Segment] on success,
     * or a [SegmentValidationException] on failure.
     */
    override fun validate(data: Segment): Result<Segment> {
        return runCatching {
            if (data.durationMillis <= SegmentValidationRules.MIN_DURATION_MILLIS) throw SegmentValidationException.DurationTooShort()
            if (data.distanceMeters < SegmentValidationRules.MIN_DISTANCE_METERS) throw SegmentValidationException.DistanceTooShort()
            if (data.pace <= SegmentValidationRules.MIN_PACE_MIN_PER_KM) throw SegmentValidationException.PaceTooFast()
            if (data.pace >= SegmentValidationRules.MAX_PACE_MIN_PER_KM) throw SegmentValidationException.PaceTooSlow()
            if (data.calories < SegmentValidationRules.MIN_CALORIES) throw SegmentValidationException.CaloriesNegative()
            data
        }
    }
}

sealed class SegmentValidationException(
    override val message: String,
) : Exception(message) {
    class DurationTooShort : SegmentValidationException("Duration must be greater than zero")

    class DistanceTooShort : SegmentValidationException("Distance must be greater than zero")

    class PaceTooSlow : SegmentValidationException("Pace is too slow")

    class PaceTooFast : SegmentValidationException("Pace is too fast")

    class CaloriesNegative : SegmentValidationException("Calories burned cannot be negative")
}
