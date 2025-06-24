package com.developers.sprintsync.presentation.workouts_stats.chart.data.processing

import com.developers.sprintsync.domain.workouts_plan.model.Metric
import com.developers.sprintsync.presentation.workouts_stats.chart.data.TimestampMetricsMap
import com.developers.sprintsync.core.util.timestamp.TimestampUtils
import com.developers.sprintsync.domain.track.model.Track

class MetricsAggregator {
    fun calculateMetricsForEachTrackingDay(tracks: List<Track>): TimestampMetricsMap {
        val timestampMetricValuesMap = mutableMapOf<Long, Map<Metric, Float>>()
        tracks.forEach { track ->
            val dayTimestampKey = TimestampUtils.getStartOfDayTimestamp(track.timestamp)
            val valuesMap = timestampMetricValuesMap[dayTimestampKey] ?: Metric.entries.associateWith { 0f }
            val updatedValuesMap = valuesMap.toMutableMap()
            updatedValuesMap.forEach { (metric, value) ->
                when (metric) {
                    Metric.DISTANCE -> updatedValuesMap[metric] = value + track.distanceMeters
                    Metric.DURATION -> updatedValuesMap[metric] = value + track.durationMillis
                    Metric.CALORIES -> updatedValuesMap[metric] = value + track.calories
                }
            }
            timestampMetricValuesMap[dayTimestampKey] = updatedValuesMap
        }
        return timestampMetricValuesMap
    }
}
