package com.developers.sprintsync.core.util.log.logger

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.track.model.Track
import javax.inject.Inject

class TrackLogger @Inject constructor(private val log: AppLogger) {
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

        log.d("Track - $trackDetails")

        SegmentLogger.log(track.segments)
    }
}
