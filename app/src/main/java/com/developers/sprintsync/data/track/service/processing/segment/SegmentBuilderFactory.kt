package com.developers.sprintsync.data.track.service.processing.segment

import com.developers.sprintsync.domain.track.model.LocationModel
import javax.inject.Inject

class SegmentBuilderFactory @Inject constructor(
    private val segmentClassifier: SegmentClassifier,
    private val activeBuilder: SegmentBuilder.ActiveSegmentBuilder,
    private val stationaryBuilder: SegmentBuilder.StationarySegmentBuilder
) {
    fun getBuilder(
        startLocation: LocationModel,
        endLocation: LocationModel,
    ): SegmentBuilder {
        val segmentType = (segmentClassifier.classifySegment(startLocation, endLocation))
        return when (segmentType) {
            SegmentType.Active -> activeBuilder
            SegmentType.Stationary -> stationaryBuilder
        }
    }
}
