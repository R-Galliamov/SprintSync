package com.developers.sprintsync.statistics.domain.chart.data.processing

import com.developers.sprintsync.core.components.goal.data.model.Metric
import com.developers.sprintsync.statistics.domain.chart.data.MetricsMap
import com.developers.sprintsync.core.util.timestamp.TimestampBuilder
import com.developers.sprintsync.core.components.goal.data.model.DailyGoal

class DailyGoalProvider(
    private val goals: List<DailyGoal>,
) {
    fun fetchGoalsForTimestamp(timestamp: Long): MetricsMap {
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
