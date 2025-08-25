package com.developers.sprintsync.domain.track.util

import com.developers.sprintsync.domain.track.model.Segment

/**
 * Smoothes out the pace of segments in a list.
 *
 * This class applies a moving average filter to the pace of each segment,
 * effectively reducing noise and creating a smoother pace profile. It also
 * limits abrupt jumps in pace between consecutive segments.
 *
 * @property smoothingWindow The size of the moving average window. A larger window
 *                           results in smoother but potentially less responsive pace.
 *
 * @property maxDeltaPace The maximum allowed change in pace (in the unit of pace, e.g., min/km)
 *                        between two consecutive smoothed segments. This helps to prevent
 *                        unrealistic sudden spikes or drops in pace.
 */
class SegmentPaceSmoother(
    private val smoothingWindow: Int = 30,
    private val maxDeltaPace: Float = 0.05f // max allowed pace change between segments
) {
    fun smooth(segments: List<Segment>): List<Segment> {
        if (segments.isEmpty() || smoothingWindow <= 1) return segments

        val smoothedSegments = segments.toMutableList()
        val window = mutableListOf<Float>()

        for ((index, segment) in segments.withIndex()) {
            window.add(segment.pace)
            if (window.size > smoothingWindow) window.removeAt(0)

            var averagePace = window.average().toFloat()

            // Limit abrupt jumps
            val prevPace = smoothedSegments.getOrNull(index - 1)?.pace ?: averagePace
            val delta = averagePace - prevPace
            if (delta > maxDeltaPace) averagePace = prevPace + maxDeltaPace
            if (delta < -maxDeltaPace) averagePace = prevPace - maxDeltaPace

            smoothedSegments[index] = segment.copy(pace = averagePace)
        }

        return smoothedSegments
    }
}
