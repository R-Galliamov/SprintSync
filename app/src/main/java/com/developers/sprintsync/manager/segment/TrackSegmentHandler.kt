package com.developers.sprintsync.manager.segment

import com.developers.sprintsync.model.tracking.GeoTimePair
import com.developers.sprintsync.model.tracking.LocationModel
import com.developers.sprintsync.model.tracking.TrackSegment
import com.developers.sprintsync.util.mapper.model.toTrackSegment
import javax.inject.Inject

class TrackSegmentHandler
    @Inject
    constructor() {
        private var previousPair: GeoTimePair? = null
        private var currentPair: GeoTimePair? = null

        fun nextSegmentOrNull(
            location: LocationModel,
            timeMillis: Long,
        ): TrackSegment? {
            previousPair = currentPair
            val geoTimePair = GeoTimePair(location, timeMillis)
            currentPair = geoTimePair
            return previousPair?.toTrackSegment(geoTimePair)
        }
    }
