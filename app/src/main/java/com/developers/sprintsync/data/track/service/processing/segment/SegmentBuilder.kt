package com.developers.sprintsync.data.track.service.processing.segment

import com.developers.sprintsync.data.track.service.processing.calculator.MetricsCalcOrchestrator
import com.developers.sprintsync.data.track.service.processing.session.TimedLocation
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.use_case.validator.SegmentValidator
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import javax.inject.Inject

/**
 * Interface for building track segments from timed location data.
 */
interface SegmentBuilder {
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
}

/**
 * Builds a segment with metrics like distance, pace, and calories.
 */
class DefaultSegmentBuilder @Inject constructor(
    private val userParameters: UserParameters,
    val calculator: MetricsCalcOrchestrator,
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
            val pace = calculator.calculatePaceMPKm(durationMillis, distanceMeters) ?: 0f // TODO find better solution
            val calories =
                calculator.calculateCalories(userParameters.weightKg, distanceMeters, durationMillis)

            val segment = Segment(
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

    fun resetAccumulatedData() {
        calculator.reset()
    }
}