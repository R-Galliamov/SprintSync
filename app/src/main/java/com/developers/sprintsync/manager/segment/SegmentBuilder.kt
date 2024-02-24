package com.developers.sprintsync.manager.segment

import com.developers.sprintsync.model.tracking.GeoTimePair
import com.developers.sprintsync.model.tracking.LocationModel
import com.developers.sprintsync.model.tracking.TrackSegment
import com.developers.sprintsync.util.mapper.model.TrackSegmentMapper
import javax.inject.Inject

class SegmentBuilder
    @Inject
    constructor(
        private val mapper: TrackSegmentMapper,
    ) {
        private var previousPair: GeoTimePair? = null
        private var currentPair: GeoTimePair? = null

        fun nextSegmentOrNull(
            location: LocationModel,
            timeMillis: Long,
        ): TrackSegment? {
            val geoTimePair = GeoTimePair(location, timeMillis)
            updatePairs(geoTimePair)
            return previousPair?.let { startData -> buildTrackSegment(startData, geoTimePair) }
        }

        fun reset() {
            previousPair = null
            currentPair = null
        }

        private fun buildTrackSegment(
            startData: GeoTimePair,
            endData: GeoTimePair,
        ): TrackSegment {
            return mapper.buildTrackSegment(startData, endData)
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
