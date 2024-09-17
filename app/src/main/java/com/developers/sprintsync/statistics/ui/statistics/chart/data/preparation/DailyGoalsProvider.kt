package com.developers.sprintsync.statistics.ui.statistics.chart.data.preparation

import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.statistics.model.chart.chartData.MetricsMap
import com.developers.sprintsync.statistics.model.chart.chartData.util.time.TimestampBuilder
import com.developers.sprintsync.statistics.model.goal.DailyGoal

class DailyGoalsProvider(
    private val goals: List<DailyGoal>,
) {
    fun getGoalsForTimestamp(timestamp: Long): MetricsMap {
        val nextDayTimestamp = TimestampBuilder(timestamp).startOfDayTimestamp().shiftTimestampByDays(1).build()

        val applicableGoals =
            goals.filter { it.timestamp <= nextDayTimestamp }.ifEmpty {
                goals.minByOrNull { it.timestamp }?.let { listOf(it) } ?: emptyList()
            }

        return Metric.entries.associateWith { metric ->
            applicableGoals.filter { it.metricType == metric }.maxByOrNull { it.timestamp }?.value ?: 0f
        }
    }
}
