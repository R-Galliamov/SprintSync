package com.developers.sprintsync.statistics.ui.statistics.chart.data.preparation

import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.statistics.model.chart.chartData.TimestampMetricsMap
import com.developers.sprintsync.statistics.model.chart.chartData.util.time.TimeUtils
import com.developers.sprintsync.tracking.session.model.track.Track

class DailyMetricsAggregator {
    fun calculateMetricsForEachTrackingDay(tracks: List<Track>): TimestampMetricsMap {
        val timestampMetricValuesMap = mutableMapOf<Long, Map<Metric, Float>>()
        tracks.forEach { track ->
            val dayTimestampKey = TimeUtils.startOfDayTimestamp(track.timestamp)
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
