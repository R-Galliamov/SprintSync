package com.developers.sprintsync.core.tracking_service.data.processing.segment

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.tracking_service.data.model.location.GeoTimePoint
import javax.inject.Inject

sealed interface SegmentBuilder {
    fun build(
        id: Long,
        startData: GeoTimePoint,
        endData: GeoTimePoint,
    ): Result<Segment>

    data class ActiveSegmentBuilder
        @Inject
        constructor(
            private val calculator: SegmentCalculator,
        ) : SegmentBuilder {
            override fun build(
                id: Long,
                startData: GeoTimePoint,
                endData: GeoTimePoint,
            ): Result<Segment> {
                val durationMillis = calculator.calculateDurationInMillis(startData.timeMillis, endData.timeMillis)
                val distanceMeters = calculator.calculateDistanceInMeters(startData.location, endData.location)

                val dataIsValid =
                    SegmentDataValidator.ActiveSegmentDataValidator(distanceMeters, durationMillis).dataIsValid

                return if (dataIsValid) {
                    val pace = calculator.calculatePaceInMinPerKm(durationMillis, distanceMeters)
                    val burnedCalories = calculator.calculateBurnedCalories(100f, 100f) // TODO provide data

                    val segment =
                        Segment.ActiveSegment(
                            id = id,
                            startLocation = startData.location,
                            startTime = startData.timeMillis,
                            endLocation = endData.location,
                            endTime = endData.timeMillis,
                            durationMillis = durationMillis,
                            distanceMeters = distanceMeters.toInt(), // TODO replace with float
                            pace = pace,
                            calories = burnedCalories,
                        )

                    Result.success(segment)
                } else {
                    Result.failure(Error())
                }
            }
        }

    data class InactiveSegmentBuilder
        @Inject
        constructor(
            private val calculator: SegmentCalculator,
        ) : SegmentBuilder {
            override fun build(
                id: Long,
                startData: GeoTimePoint,
                endData: GeoTimePoint,
            ): Result<Segment> {
                val durationMillis = calculator.calculateDurationInMillis(startData.timeMillis, endData.timeMillis)
                val isDataValid = SegmentDataValidator.InactiveSegmentDataValidator(durationMillis).dataIsValid

                return if (isDataValid) {
                    val segment =
                        Segment.InactiveSegment(
                            id = id,
                            location = startData.location,
                            startTime = startData.timeMillis,
                            endTime = endData.timeMillis,
                            durationMillis = durationMillis,
                        )
                    Result.success(segment)
                } else {
                    Result.failure(Error())
                }
            }
        }
}