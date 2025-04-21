package com.developers.sprintsync.domain.track.use_case.validator

import com.developers.sprintsync.domain.track.model.Segment

object SegmentValidator {
    private const val MIN_DISTANCE_METERS = 0
    private const val MIN_DURATION_MILLIS = 0
    private const val MIN_PACE_MIN_PER_KM = 2
    private const val MAX_PACE_MIN_PER_KM = 30
    private const val MIN_CALORIES = 0

    fun validateOrThrow(data: Segment): Segment {
        if (data.durationMillis <= MIN_DURATION_MILLIS) throw SegmentValidationException.DurationTooShort()
        if (data is Segment.Active) {
            if (data.distanceMeters < MIN_DISTANCE_METERS) throw SegmentValidationException.DistanceTooShort()
            if (data.pace <= MIN_PACE_MIN_PER_KM) throw SegmentValidationException.PaceTooFast()
            if (data.pace >= MAX_PACE_MIN_PER_KM) throw SegmentValidationException.PaceTooSlow()
            if (data.calories < MIN_CALORIES) throw SegmentValidationException.CaloriesNegative()
        }
        return data
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
