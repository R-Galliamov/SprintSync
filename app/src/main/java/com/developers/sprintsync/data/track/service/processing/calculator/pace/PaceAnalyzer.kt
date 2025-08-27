package com.developers.sprintsync.data.track.service.processing.calculator.pace

import com.developers.sprintsync.data.track.service.processing.session.TrackPoint

data class PaceState(
    val speedMps: Float,              // smoothed speed, m/s
    val paceMinPerKm: Float?,         // pace in min/km (null if not moving)
    val totalDistanceM: Float,        // accumulated distance, meters
    val totalTimeSec: Float,          // total elapsed time, seconds
) {
    companion object {
        val EMPTY = PaceState(0f, null, 0f, 0f)
    }
}

interface RunPaceAnalyzer {

    val snapshot: PaceState

    fun reset()

    fun add(tp: TrackPoint): PaceState
}