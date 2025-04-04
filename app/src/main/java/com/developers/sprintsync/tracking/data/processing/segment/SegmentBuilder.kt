package com.developers.sprintsync.tracking.data.processing.segment

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.tracking.data.model.TimedLocation
import com.developers.sprintsync.domain.track.validation.segment.SegmentValidator
import javax.inject.Inject

sealed interface SegmentBuilder {
    fun build(
        id: Long,
        startData: TimedLocation,
        endData: TimedLocation,
    ): Result<Segment>

    class ActiveSegmentBuilder
        @Inject
        constructor(
            private val calculator: SegmentCalculator,
        ) : SegmentBuilder {
            override fun build(
                id: Long,
                startData: TimedLocation,
                endData: TimedLocation,
            ): Result<Segment> =
                runCatching {
                    val durationMillis =
                        calculator.calculateDurationInMillis(startData.timestampMillis, endData.timestampMillis)
                    val distanceMeters = calculator.calculateDistanceInMeters(startData.location, endData.location)
                    val pace = calculator.calculatePaceInMinPerKm(durationMillis, distanceMeters)
                    val burnedCalories = calculator.calculateBurnedCalories(distanceMeters, durationMillis)

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

                    SegmentValidator.validateOrThrow(segment)

                    return Result.success(segment)
                }
        }

    class StationarySegmentBuilder
        @Inject
        constructor(
            private val calculator: SegmentCalculator,
        ) : SegmentBuilder {
            override fun build(
                id: Long,
                startData: TimedLocation,
                endData: TimedLocation,
            ): Result<Segment> =
                runCatching {
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

                    SegmentValidator.validateOrThrow(segment)

                    return Result.success(segment)
                }
        }
}
