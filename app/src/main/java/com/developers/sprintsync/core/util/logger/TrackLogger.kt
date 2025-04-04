package com.developers.sprintsync.core.util.logger

import android.util.Log
import com.developers.sprintsync.domain.track.model.Track

object TrackLogger {
    fun log(track: Track) {

        val trackDetails =
            buildString {
                append("ID: ${track.id}, ")
                append("Timestamp: ${track.timestamp}, ")
                append("Duration: ${track.durationMillis}ms, ")
                append("Distance: ${track.distanceMeters}m, ")
                append("Avg Pace: ${track.avgPace}, ")
                append("Best Pace: ${track.bestPace}, ")
                append("Calories: ${track.calories}")
            }

        Log.d(TAG, "Track - $trackDetails")

        SegmentLogger.log(track.segments)
    }

    private const val TAG = "My stack: TrackLogger"
}
