package com.developers.sprintsync.tracking.data.processing.segment

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.util.validation.ValidationResult
import com.developers.sprintsync.tracking.data.model.TimedLocation
import com.developers.sprintsync.tracking.data.processing.util.validator.SegmentValidator
import javax.inject.Inject

sealed interface SegmentBuilder {
    fun build(
        id: Long,
        startData: TimedLocation,
        endData: TimedLocation,
    ): Result<Segment>

    data class ActiveSegmentBuilder
        @Inject
        constructor(
            private val calculator: SegmentCalculator,
        ) : SegmentBuilder {
            override fun build(
                id: Long,
                startData: TimedLocation,
                endData: TimedLocation,
            ): Result<Segment> {
                val durationMillis =
                    calculator.calculateDurationInMillis(startData.timestampMillis, endData.timestampMillis)
                val distanceMeters = calculator.calculateDistanceInMeters(startData.location, endData.location)
                val pace = calculator.calculatePaceInMinPerKm(durationMillis, distanceMeters)
                val burnedCalories = calculator.calculateBurnedCalories(100f, 100f) // TODO provide data

                val segment =
                    Segment.Active(
                        id = id,
                        startLocation = startData.location,
                        startTime = startData.timestampMillis,
                        endLocation = endData.location,
                        endTime = endData.timestampMillis,
                        durationMillis = durationMillis,
                        distanceMeters = distanceMeters,
                        pace = pace,
                        calories = burnedCalories,
                    )

                return when (val validationResult = SegmentValidator.validate(segment)) {
                    ValidationResult.Valid -> Result.success(segment)
                    is ValidationResult.Invalid -> Result.failure(validationResult.exception)
                }
            }
        }

    data class StationarySegmentBuilder
        @Inject
        constructor(
            private val calculator: SegmentCalculator,
        ) : SegmentBuilder {
            override fun build(
                id: Long,
                startData: TimedLocation,
                endData: TimedLocation,
            ): Result<Segment> {
                val durationMillis =
                    calculator.calculateDurationInMillis(startData.timestampMillis, endData.timestampMillis)

                val segment =
                    Segment.Stationary(
                        id = id,
                        location = startData.location,
                        startTime = startData.timestampMillis,
                        endTime = endData.timestampMillis,
                        durationMillis = durationMillis,
                    )

                return when (val validationResult = SegmentValidator.validate(segment)) {
                    ValidationResult.Valid -> Result.success(segment)
                    is ValidationResult.Invalid -> Result.failure(validationResult.exception)
                }
            }
        }
}
