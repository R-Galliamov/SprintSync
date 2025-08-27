package com.developers.sprintsync.data.track.service.processing.segment

import com.developers.sprintsync.data.track.service.processing.calculator.MetricsCalcOrchestrator
import com.developers.sprintsync.data.track.service.processing.calculator.pace.RunPaceAnalyzer
import com.developers.sprintsync.data.track.service.processing.session.TrackPoint
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.validator.Validator
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import javax.inject.Inject

/**
 * Interface for building track segments from timed location data.
 */
interface SegmentBuilder {
    /**
     * Builds a segment from start and end location data.
     * @param id Unique identifier for the segment.
     * @param startPoint Starting timed location of the segment.
     * @param endPoint Ending timed location of the segment.
     * @return Result containing the built [Segment] or an error if building fails.
     */
    fun build(
        id: Long,
        startPoint: TrackPoint,
        endPoint: TrackPoint,
    ): Result<Segment>
}

/**
 * Builds a segment with metrics like distance, pace, and calories.
 */
class DefaultSegmentBuilder @Inject constructor(
    val calculator: MetricsCalcOrchestrator,
    private val userParameters: UserParameters,
    private val validator: Validator<Segment>,
    private val paceAnalyzer: RunPaceAnalyzer
) : SegmentBuilder {
    /**
     * Constructs an active segment with calculated metrics.
     * @param id Unique identifier for the segment.
     * @param startPoint Starting timed location.
     * @param endPoint Ending timed location.
     * @return Result containing the [Segment] or an error if calculations or validation fail.
     */
    override fun build(
        id: Long,
        startPoint: TrackPoint,
        endPoint: TrackPoint,
    ): Result<Segment> =
        runCatching {
            val durationMillis =
                calculator.calculateDurationMillis(startPoint.timeMs, endPoint.timeMs)
            val distanceMeters = calculator.calculateDistanceMeters(startPoint.location, endPoint.location)
            if (paceAnalyzer.snapshot.paceMinPerKm == null) {
                paceAnalyzer.add(startPoint).paceMinPerKm
            }
            val pace = paceAnalyzer.add(endPoint).paceMinPerKm ?: 0f
            val calories =
                calculator.calculateCalories(userParameters.weightKg, distanceMeters, durationMillis)

            val segment = Segment(
                id = id,
                startLocation = startPoint.location,
                startTime = startPoint.timeMs,
                endLocation = endPoint.location,
                endTime = endPoint.timeMs,
                durationMillis = durationMillis,
                distanceMeters = distanceMeters,
                pace = pace,
                calories = calories,
            )

            return validator.validate(segment)
        }
}