package com.developers.sprintsync.domain.track.validator

import com.developers.sprintsync.domain.track.model.Segment

object MetricValidationRules {
    const val MIN_DISTANCE_METERS = 0
    const val MAX_DISTANCE_METERS = 20
    const val MIN_DURATION_MILLIS = 0
    const val MIN_PACE_MIN_PER_KM = 2
    const val MAX_PACE_MIN_PER_KM = 20
    const val MIN_CALORIES = 0
}

class MetricValidator {

    fun validateDistance(distM: Float): Set<SegmentValidationException> {
        val errors = mutableSetOf<SegmentValidationException>()

        if (distM < MetricValidationRules.MIN_DISTANCE_METERS) errors += SegmentValidationException.DistanceTooShort()
        if (distM > MetricValidationRules.MAX_DISTANCE_METERS) errors += SegmentValidationException.DistanceTooLong()

        return errors
    }

    fun validatePace(paceMpk: Float): Set<SegmentValidationException> {
        val errors = mutableSetOf<SegmentValidationException>()

        if (paceMpk <= MetricValidationRules.MIN_PACE_MIN_PER_KM) errors += SegmentValidationException.PaceTooFast()
        if (paceMpk >= MetricValidationRules.MAX_PACE_MIN_PER_KM) errors += SegmentValidationException.PaceTooSlow()

        return errors
    }

    fun validateSegment(segment: Segment): Set<SegmentValidationException> {
        val errors = mutableSetOf<SegmentValidationException>()

        if (segment.durationMillis <= MetricValidationRules.MIN_DURATION_MILLIS) errors += SegmentValidationException.DurationTooShort()
        validateDistance(segment.distanceMeters)
        if (segment.calories < MetricValidationRules.MIN_CALORIES) errors += SegmentValidationException.CaloriesNegative()

        segment.pace?.let { pace ->
            errors += validatePace(pace)
        }

        return errors
    }
}

sealed class SegmentValidationException(
    override val message: String,
) : Exception(message) {
    class DurationTooShort : SegmentValidationException("Duration must be greater than zero")

    class DistanceTooShort : SegmentValidationException("Distance must be greater than zero")

    class DistanceTooLong : SegmentValidationException("Distance mustn't be so long ")

    class PaceTooSlow : SegmentValidationException("Pace is too slow")

    class PaceTooFast : SegmentValidationException("Pace is too fast")

    class CaloriesNegative : SegmentValidationException("Calories burned cannot be negative")
}
