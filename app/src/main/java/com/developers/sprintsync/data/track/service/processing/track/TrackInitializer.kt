package com.developers.sprintsync.data.track.service.processing.track

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track
import javax.inject.Inject

class TrackInitializer
    @Inject
    constructor() {
        companion object {
            private const val DEFAULT_ID = 0
        }

        fun initializeTrack(initialSegment: Segment): Track =
            when (initialSegment) {
                is Segment.Active ->
                    Track(
                        id = DEFAULT_ID,
                        timestamp = System.currentTimeMillis() - initialSegment.startTime,
                        durationMillis = initialSegment.startTime + initialSegment.durationMillis,
                        distanceMeters = initialSegment.distanceMeters,
                        avgPace = initialSegment.pace,
                        bestPace = initialSegment.pace,
                        calories = initialSegment.calories,
                        segments = listOf(initialSegment),
                    )

                is Segment.Stationary ->
                    Track.INITIAL.copy(
                        id = DEFAULT_ID,
                        timestamp = System.currentTimeMillis() - initialSegment.startTime,
                        segments = listOf(initialSegment),
                    )
            }
    }
