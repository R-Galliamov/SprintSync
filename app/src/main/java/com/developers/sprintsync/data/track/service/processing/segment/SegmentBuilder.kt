package com.developers.sprintsync.data.track.service.processing.segment

import com.developers.sprintsync.data.track.service.processing.calculator.SegmentMetricsCalculator
import com.developers.sprintsync.data.track.service.processing.session.TimedLocation
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.use_case.validator.SegmentValidator
import com.developers.sprintsync.domain.user_parameters.model.UserParameters
import javax.inject.Inject

/**
 * Interface for building track segments from timed location data.
 */
sealed interface SegmentBuilder {
    /**
     * Builds a segment from start and end location data.
     * @param id Unique identifier for the segment.
     * @param startData Starting timed location of the segment.
     * @param endData Ending timed location of the segment.
     * @return Result containing the built [Segment] or an error if building fails.
     */
    fun build(
        id: Long,
        startData: TimedLocation,
        endData: TimedLocation,
    ): Result<Segment>

    /**
     * Builds an active segment with metrics like distance, pace, and calories.
     */
    class ActiveSegmentBuilder @Inject constructor(
        private val userParameters: UserParameters,
        private val calculator: SegmentMetricsCalculator,
    ) : SegmentBuilder {

        /**
         * Constructs an active segment with calculated metrics.
         * @param id Unique identifier for the segment.
         * @param startData Starting timed location.
         * @param endData Ending timed location.
         * @return Result containing the [Segment.Active] or an error if calculations or validation fail.
         */
        override fun build(
            id: Long,
            startData: TimedLocation,
            endData: TimedLocation,
        ): Result<Segment> =
            runCatching {
                val durationMillis =
                    calculator.calculateDurationMillis(startData.timestampMillis, endData.timestampMillis)
                val distanceMeters = calculator.calculateDistanceMeters(startData.location, endData.location)
                val pace = calculator.calculatePaceMPKm(durationMillis, distanceMeters)
                val calories =
                    calculator.calculateCalories(userParameters.weightKg, distanceMeters, durationMillis)

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
                        calories = calories,
                    )

                SegmentValidator.validateOrThrow(segment)

                return Result.success(segment)
            }
    }

    /**
     * Builds a stationary segment with duration but no movement.
     */
    class StationarySegmentBuilder @Inject constructor(
        private val calculator: SegmentMetricsCalculator
    ) : SegmentBuilder {

        /**
         * Constructs a stationary segment with duration.
         * @param id Unique identifier for the segment.
         * @param startData Starting timed location.
         * @param endData Ending timed location.
         * @return Result containing the [Segment.Stationary] or an error if calculations or validation fail.
         */
        override fun build(
            id: Long,
            startData: TimedLocation,
            endData: TimedLocation,
        ): Result<Segment> =
            runCatching {
                val durationMillis =
                    calculator.calculateDurationMillis(startData.timestampMillis, endData.timestampMillis)

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
