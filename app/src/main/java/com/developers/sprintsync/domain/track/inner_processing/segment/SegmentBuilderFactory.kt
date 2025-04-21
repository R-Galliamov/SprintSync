package com.developers.sprintsync.domain.track.inner_processing.segment

import com.developers.sprintsync.domain.track.model.LocationModel

class SegmentBuilderFactory(userWeightKilos: Float) {

    private val segmentClassifier = SegmentClassifier()
    private val activeBuilder = SegmentBuilder.ActiveSegmentBuilder(userWeightKilos)
    private val stationaryBuilder = SegmentBuilder.StationarySegmentBuilder()

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
