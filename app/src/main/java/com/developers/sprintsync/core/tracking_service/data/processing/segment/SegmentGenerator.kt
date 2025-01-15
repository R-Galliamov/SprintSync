package com.developers.sprintsync.core.tracking_service.data.processing.segment

import android.util.Log
import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.tracking_service.data.model.location.GeoTimePoint
import com.developers.sprintsync.core.tracking_service.data.model.location.LocationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class SegmentGenerator
    @Inject
    constructor(
        private val activeSegmentBuilder: SegmentBuilder.ActiveSegmentBuilder,
        private val inactiveSegmentBuilder: SegmentBuilder.InactiveSegmentBuilder,
    ) {
        private var state: SegmentGeneratorState = SegmentGeneratorState.Uninitialized

        private val _data = MutableStateFlow<Segment?>(null)
        val data = _data.asStateFlow().filterNotNull()

        fun addPoint(
            type: SegmentType,
            location: LocationModel,
            timeMillis: Long,
        ) {
            val newData = GeoTimePoint(location, timeMillis)
            when (val currentState = state) {
                SegmentGeneratorState.Uninitialized -> {
                    transitionToPendingSegment(newData)
                }

                is SegmentGeneratorState.Initialized -> {
                    val segmentId = getNextSegmentId(currentState)
                    val startData = currentState.lastDataPoint
                    val builder = getSegmentBuilder(type)
                    generateSegment(builder, segmentId, startData, newData) { segment ->
                        state = SegmentGeneratorState.GeneratingSegments(newData, segment, segmentId)
                        _data.update { segment }
                    }
                }
            }
        }

        fun resetData() {
            state = SegmentGeneratorState.Uninitialized
        }

        private fun transitionToPendingSegment(dataPoint: GeoTimePoint) {
            state = SegmentGeneratorState.Initialized(dataPoint)
        }

        private fun generateSegment(
            builder: SegmentBuilder,
            segmentId: Long,
            startData: GeoTimePoint,
            endData: GeoTimePoint,
            onSuccess: (Segment) -> Unit,
        ) {
            builder.build(segmentId, startData, endData).fold(
                onSuccess = { segment ->
                    onSuccess(segment)
                },
                onFailure = { error ->
                    Log.e(
                        "SegmentGenerator",
                        "Failed to build segment: ${error.message}. Start: $startData, End: $endData",
                    )
                },
            )
        }

        private fun getNextSegmentId(currentState: SegmentGeneratorState): Long =
            when (currentState) {
                is SegmentGeneratorState.GeneratingSegments -> (currentState.segmentId + 1)
                else -> 0
            }

        private fun getSegmentBuilder(type: SegmentType): SegmentBuilder =
            when (type) {
                SegmentType.ACTIVE -> activeSegmentBuilder
                SegmentType.INACTIVE -> inactiveSegmentBuilder
            }
    }
