package com.developers.sprintsync.user.ui.userProfile.chart.data.preparation

import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.chart.chartData.Metric
import com.developers.sprintsync.user.model.chart.chartData.MetricsMap
import com.developers.sprintsync.user.model.chart.chartData.TimestampMetricsMap
import java.util.Calendar

class DailyMetricsAggregator {
    fun calculateMetricsForEachTrackingDay(tracks: List<Track>): TimestampMetricsMap {
        val dailyValuesMap = mutableMapOf<Long, Map<Metric, Float>>()
        tracks.forEach { track ->
            val dayTimestampKey = setTimeToStartOfDay(track.timestamp)
            val valuesMap: MetricsMap = dailyValuesMap[dayTimestampKey] ?: emptyMap()
            val updatedValuesMap = valuesMap.toMutableMap()
            updatedValuesMap[Metric.DISTANCE] =
                updatedValuesMap.getOrDefault(Metric.DISTANCE, 0f) + track.distanceMeters
            updatedValuesMap[Metric.DURATION] =
                updatedValuesMap.getOrDefault(Metric.DURATION, 0f) + track.durationMillis
            dailyValuesMap[dayTimestampKey] = updatedValuesMap
        }
        return dailyValuesMap
    }

    private fun setTimeToStartOfDay(timestamp: Long): Long {
        val calendar =
            Calendar.getInstance().apply { timeInMillis = timestamp }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}