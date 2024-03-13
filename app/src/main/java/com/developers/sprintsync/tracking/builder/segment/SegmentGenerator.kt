package com.developers.sprintsync.tracking.builder.segment

import com.developers.sprintsync.tracking.mapper.model.TrackSegmentMapper
import com.developers.sprintsync.tracking.model.GeoTimePair
import com.developers.sprintsync.tracking.model.LocationModel
import com.developers.sprintsync.tracking.model.TrackSegment
import javax.inject.Inject

class SegmentGenerator
    @Inject
    constructor(
        private val mapper: TrackSegmentMapper,
    ) {
        private var segmentId: Long = 0
        private var previousPair: GeoTimePair? = null
        private var currentPair: GeoTimePair? = null

        // TODO fix crash when location is not changed and come at the same time
        fun nextSegmentOrNull(
            location: LocationModel,
            timeMillis: Long,
        ): TrackSegment? {
            if (!checkLocationChanged(location)) return null
            val geoTimePair = GeoTimePair(location, timeMillis)
            updatePairs(geoTimePair)
            return previousPair?.let { startData ->
                buildTrackSegment(
                    segmentId,
                    startData,
                    geoTimePair,
                )
            }
                .also { segmentId++ }
        }

        fun reset() {
            previousPair = null
            currentPair = null
        }

        private fun checkLocationChanged(location: LocationModel): Boolean {
            currentPair?.let { return it.location != location }
            previousPair?.let { return it.location != location }
            return true
        }

        private fun buildTrackSegment(
            segmentId: Long,
            startData: GeoTimePair,
            endData: GeoTimePair,
        ): TrackSegment {
            return mapper.buildTrackSegment(segmentId, startData, endData)
        }

        private fun updatePairs(geoTimePair: GeoTimePair) {
            updatePreviousPair()
            updateCurrentPair(geoTimePair)
        }

        private fun updatePreviousPair() {
            previousPair = currentPair
        }

        private fun updateCurrentPair(geoTimePair: GeoTimePair) {
            currentPair = geoTimePair
        }
    }
