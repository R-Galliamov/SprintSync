package com.developers.sprintsync.updater

import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.model.TrackSegment
import javax.inject.Inject

class TrackCreator
    @Inject
    constructor() {
        fun createTrackData(initialSegment: TrackSegment): Track {
            return Track(
                id = 0,
                startTimeDateMillis = System.currentTimeMillis(),
                durationMillis = initialSegment.durationMillis,
                distanceMeters = initialSegment.distanceMeters,
                avgPace = initialSegment.pace,
                maxPace = initialSegment.pace,
                calories = initialSegment.burnedKCalories,
                segments = listOf(initialSegment),
            )
        }
    }
