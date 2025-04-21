package com.developers.sprintsync.domain.track.inner_processing.segment

import com.developers.sprintsync.data.track.service.processing.session.TimedLocation
import com.developers.sprintsync.domain.track.inner_processing.calculator.SegmentMetricsCalculator
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.use_case.validator.SegmentValidator
import javax.inject.Inject

sealed interface SegmentBuilder {
    fun build(
        id: Long,
        startData: TimedLocation,
        endData: TimedLocation,
    ): Result<Segment>

    class ActiveSegmentBuilder(
        private val userWeightKilos: Float,
    ) : SegmentBuilder {
        private val calculator = SegmentMetricsCalculator()

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
                val burnedCalories =
                    calculator.calculateBurnedCalories(userWeightKilos, distanceMeters, durationMillis)

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
        constructor() : SegmentBuilder {
            private val calculator = SegmentMetricsCalculator()

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
