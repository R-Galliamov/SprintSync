package com.developers.sprintsync.core.tracking_service.data.processing.track

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Track
import javax.inject.Inject

class TrackInitializer
    @Inject
    constructor() {
        companion object {
            private const val DEFAULT_ID = 0
        }

        fun initializeTrack(initialSegment: Segment): Track =
            when (initialSegment) {
                is Segment.ActiveSegment ->
                    Track(
                        id = DEFAULT_ID,
                        timestamp = System.currentTimeMillis() - initialSegment.startTime,
                        durationMillis = initialSegment.startTime + initialSegment.durationMillis,
                        distanceMeters = initialSegment.distanceMeters,
                        avgPace = initialSegment.pace,
                        bestPace = initialSegment.pace,
                        calories = initialSegment.calories,
                        segments = listOf(initialSegment),
                        mapPreview = null,
                    )

                is Segment.InactiveSegment ->
                    Track.EMPTY_TRACK_DATA.copy(
                        id = DEFAULT_ID,
                        timestamp = System.currentTimeMillis() - initialSegment.startTime,
                        segments = listOf(initialSegment),
                    )
            }
    }
