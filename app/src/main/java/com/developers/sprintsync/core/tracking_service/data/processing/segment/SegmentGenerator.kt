package com.developers.sprintsync.core.tracking_service.data.processing.segment

import com.developers.sprintsync.core.tracking_service.data.model.location.GeoTimePoint
import com.developers.sprintsync.core.tracking_service.data.model.location.LocationModel
import com.developers.sprintsync.core.components.track.data.model.Segment
import javax.inject.Inject

class SegmentGenerator
    @Inject
    constructor(
        private val builder: SegmentBuilder,
    ) {
        private var nextSegmentId: Long = 0
        private var lastDataPoint: GeoTimePoint? = null
        private var currentSegmentValue: Segment? = null

        fun addActiveDataPoint(
            location: LocationModel,
            timeMillis: Long,
        ) {
            if (location == lastDataPoint?.location) return
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

        fun getCurrentSegment(): Segment? = currentSegmentValue

        private fun createActiveSegment(data: GeoTimePoint) {
            lastDataPoint?.let { startData ->
                val segment = builder.buildActiveTrackSegment(nextSegmentId, startData, data)
                setCurrentSegmentValue(segment)
                incrementNextSegmentId()
            }
        }

        private fun createInactiveSegment(endTimeMillis: Long) {
            lastDataPoint?.let { startData ->
                val segment = builder.buildInactiveSegment(nextSegmentId, startData, endTimeMillis)
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
