package com.developers.sprintsync.tracking.service.monitor

import com.developers.sprintsync.tracking.model.indicator.PaceAnalysisResult
import com.developers.sprintsync.tracking.model.track.Segment

class PaceAnalyzer {
    companion object {
        private const val REQUIRED_VALUES_COUNT = 5
        private const val SLOW_DOWN_PACE = 15f

        fun analyzePaceChange(segments: List<Segment>): PaceAnalysisResult {
            if (hasInsufficientValues(segments)) {
                return PaceAnalysisResult.InsufficientData
            }
            if (!areRecentSegmentsActive(segments)) {
                return PaceAnalysisResult.InvalidData
            }

            val recentPaceValues = getRecentPace(segments)
            return when {
                hasZeroValues(recentPaceValues) -> PaceAnalysisResult.InvalidData
                isPaceSlowedDown(recentPaceValues) -> PaceAnalysisResult.PaceSlowedDown
                else -> PaceAnalysisResult.PaceNormal
            }
        }

        private fun getRecentPace(segments: List<Segment>): List<Float> =
            segments
                .filterIsInstance<Segment.ActiveSegment>()
                .takeLast(REQUIRED_VALUES_COUNT)
                .map { segment -> segment.pace }

        private fun hasInsufficientValues(values: List<*>): Boolean = values.size < REQUIRED_VALUES_COUNT

        private fun hasZeroValues(values: List<Float>): Boolean =
            values.any {
                it == 0f
            }

        private fun isPaceSlowedDown(recentPaceValues: List<Float>): Boolean = recentPaceValues.all { isPaceSlowedDown(it) }

        private fun isPaceSlowedDown(pace: Float): Boolean = pace > SLOW_DOWN_PACE

        private fun areRecentSegmentsActive(segments: List<Segment>): Boolean =
            segments.takeLast(REQUIRED_VALUES_COUNT).all {
                it is Segment.ActiveSegment
            }
    }
}
