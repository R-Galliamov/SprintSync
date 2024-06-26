package com.developers.sprintsync.tracking.service.builder.segment

import com.developers.sprintsync.tracking.mapper.model.SegmentMapper
import com.developers.sprintsync.tracking.model.GeoTimePoint
import com.developers.sprintsync.tracking.model.LocationModel
import com.developers.sprintsync.tracking.model.Segment
import javax.inject.Inject

class SegmentBuilder
    @Inject
    constructor(
        private val mapper: SegmentMapper,
    ) {
        private var nextSegmentId: Long = 0
        private var lastDataPoint: GeoTimePoint? = null
        private var currentSegmentValue: Segment? = null

        fun addActiveDataPoint(
            location: LocationModel,
            timeMillis: Long,
        ) {
            // TODO: check if location changed
            val newData = GeoTimePoint(location, timeMillis)
            createActiveSegment(newData)
            setLastDataPoint(newData)
        }

        fun addInactiveDataPoint(endPauseTimeMillis: Long) {
            createInactiveSegment(endPauseTimeMillis)
        }

        fun clearLastData() {
            setLastDataPoint(null)
            setCurrentSegmentValue(null)
        }

        fun getCurrentSegment(): Segment? {
            return currentSegmentValue
        }

        private fun createActiveSegment(data: GeoTimePoint) {
            lastDataPoint?.let { startData ->
                val segment = mapper.buildActiveTrackSegment(nextSegmentId, startData, data)
                setCurrentSegmentValue(segment)
                incrementNextSegmentId()
            }
        }

        private fun createInactiveSegment(endTimeMillis: Long) {
            lastDataPoint?.let { startData ->
                val segment = mapper.buildInactiveSegment(nextSegmentId, startData, endTimeMillis)
                setCurrentSegmentValue(segment)
                incrementNextSegmentId()
            }
        }

        private fun setLastDataPoint(value: GeoTimePoint?) {
            lastDataPoint = value
        }

        private fun setCurrentSegmentValue(value: Segment?) {
            currentSegmentValue = value
        }

        private fun incrementNextSegmentId() {
            nextSegmentId++
        }
    }
