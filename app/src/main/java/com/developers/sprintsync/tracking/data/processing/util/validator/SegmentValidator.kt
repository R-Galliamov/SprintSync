package com.developers.sprintsync.tracking.data.processing.util.validator

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.util.validation.ValidationException
import com.developers.sprintsync.core.util.validation.Validator

object SegmentValidator : Validator<Segment> {
    private const val MIN_DISTANCE_METERS = 0
    private const val MIN_DURATION_MILLIS = 0
    private const val MIN_PACE_MIN_PER_KM = 2
    private const val MAX_PACE_MIN_PER_KM = 30
    private const val MIN_CALORIES = 0

    override fun validateOrThrow(data: Segment): Segment {
        val errors = mutableListOf<SegmentValidationError>()

        if (data.durationMillis <= MIN_DURATION_MILLIS) errors.add(SegmentValidationError.DurationTooShort)
        if (data is Segment.Active) {
            if (data.distanceMeters < MIN_DISTANCE_METERS) errors.add(SegmentValidationError.DistanceTooShort)
            if (data.pace <= MIN_PACE_MIN_PER_KM) errors.add(SegmentValidationError.PaceTooFast)
            if (data.pace >= MAX_PACE_MIN_PER_KM) errors.add(SegmentValidationError.PaceTooSlow)
            if (data.calories < MIN_CALORIES) errors.add(SegmentValidationError.CaloriesNegative)
        }

        if (errors.isNotEmpty()) throw ValidationException(data, errors)

        return data
    }
}
