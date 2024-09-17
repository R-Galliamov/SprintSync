package com.developers.sprintsync.statistics.dataStorage.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.statistics.model.goal.DailyGoal

@Entity
data class DailyGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val metricType: Metric,
    val value: Long,
) {
    fun toDto() = DailyGoal(timestamp = timestamp, metricType = metricType, value = value.toFloat())

    companion object {
        fun fromDto(dto: DailyGoal): DailyGoalEntity =
            DailyGoalEntity(
                id = dto.id,
                timestamp = dto.timestamp,
                metricType = dto.metricType,
                value = dto.value.toLong(),
            )
    }
}
