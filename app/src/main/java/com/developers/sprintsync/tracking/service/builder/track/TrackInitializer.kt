package com.developers.sprintsync.tracking.service.builder.track

import com.developers.sprintsync.tracking.model.Segment
import com.developers.sprintsync.tracking.model.Track
import javax.inject.Inject

class TrackInitializer
    @Inject
    constructor() {
        companion object {
            private const val DEFAULT_ID = -1
        }

        fun initializeTrack(initialSegment: Segment): Track {
            return when (initialSegment) {
                is Segment.ActiveSegment ->
                    Track(
                        id = DEFAULT_ID,
                        startTimeDateMillis = System.currentTimeMillis() - initialSegment.startTime,
                        durationMillis = initialSegment.durationMillis,
                        distanceMeters = initialSegment.distanceMeters,
                        avgPace = initialSegment.pace,
                        maxPace = initialSegment.pace,
                        calories = initialSegment.calories,
                        segments = listOf(initialSegment),
                    )

                is Segment.InactiveSegment ->
                    Track.EMPTY_TRACK_DATA.copy(
                        id = DEFAULT_ID,
                        startTimeDateMillis = System.currentTimeMillis() - initialSegment.startTime,
                        segments = listOf(initialSegment),
                    )
            }
        }
    }
