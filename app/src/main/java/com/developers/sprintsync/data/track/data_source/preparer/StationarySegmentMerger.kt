package com.developers.sprintsync.data.track.data_source.preparer

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Segments
import javax.inject.Inject

class StationarySegmentMerger
    @Inject
    constructor() {
        fun mergeStationarySegments(segments: Segments): Segments {
            if (segments.isEmpty()) return emptyList()

            val result = mutableListOf<Segment>()
            var pendingStationary: Segment.Stationary? = null
            var nextId = 0L

            for (segment in segments) {
                when (segment) {
                    is Segment.Active -> {
                        pendingStationary?.let {
                            result.add(it.copy(id = nextId++))
                        }
                        pendingStationary = null
                        result.add(segment.copy(id = nextId++))
                    }

                    is Segment.Stationary -> {
                        pendingStationary =
                            if (pendingStationary == null) {
                                segment
                            } else {
                                mergeStationary(pendingStationary, segment)
                            }
                    }
                }
            }

            pendingStationary?.let { result.add(it.copy(id = nextId++)) }

            return result
        }

        private fun mergeStationary(
            first: Segment.Stationary,
            second: Segment.Stationary,
        ): Segment.Stationary {
            val mergedEndTime = first.endTime + second.endTime
            val mergedDuration = first.durationMillis + second.durationMillis
            return first.copy(endTime = mergedEndTime, durationMillis = mergedDuration)
        }
    }