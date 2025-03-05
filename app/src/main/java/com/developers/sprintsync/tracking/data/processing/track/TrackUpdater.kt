package com.developers.sprintsync.tracking.data.processing.track

import android.util.Log
import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Track
import javax.inject.Inject

class TrackUpdater
    @Inject
    constructor(
        private val calculator: TrackCalculator,
    ) {
        fun updateTrackWithSegment(
            track: Track,
            segment: Segment,
        ): Track {
            val duration = calculator.calculateDuration(track, segment)
            val distance = calculator.calculateDistance(track, segment)
            val avgPace = calculator.calculateAvgPace(track, segment)
            val bestPace = calculator.calculateBestPace(track, segment)
            val calories = calculator.calculateCalories(track, segment)
            val segments = track.segments + segment
            return track.copy(
                durationMillis = duration,
                distanceMeters = distance,
                avgPace = avgPace,
                bestPace = bestPace,
                calories = calories,
                segments = segments,
            )
        }
    }
