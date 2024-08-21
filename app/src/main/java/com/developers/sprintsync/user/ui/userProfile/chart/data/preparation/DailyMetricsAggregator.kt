package com.developers.sprintsync.user.ui.userProfile.chart.data.preparation

import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.chart.chartData.Metric
import com.developers.sprintsync.user.model.chart.chartData.TimestampMetricsMap
import com.developers.sprintsync.user.model.chart.chartData.util.time.TimeUtils

class DailyMetricsAggregator {
    fun calculateMetricsForEachTrackingDay(tracks: List<Track>): TimestampMetricsMap {
        val timestampMetricValuesMap = mutableMapOf<Long, Map<Metric, Float>>()
        tracks.forEach { track ->
            val dayTimestampKey = TimeUtils.startOfDayTimestamp(track.timestamp)
            val valuesMap = (
                timestampMetricValuesMap[dayTimestampKey] ?: mapOf(
                    Metric.DISTANCE to 0f,
                    Metric.DURATION to 0f,
                )
            )
            val updatedValuesMap = valuesMap.toMutableMap()
            updatedValuesMap.forEach { (metric, value) ->
                when (metric) {
                    Metric.DISTANCE -> {
                        updatedValuesMap[metric] = value + track.distanceMeters
                    }

                    Metric.DURATION -> {
                        updatedValuesMap[metric] = value + track.durationMillis
                    }
                }
            }
            timestampMetricValuesMap[dayTimestampKey] = updatedValuesMap
        }
        return timestampMetricValuesMap
    }
}
