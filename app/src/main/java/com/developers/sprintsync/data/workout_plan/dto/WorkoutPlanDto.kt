package com.developers.sprintsync.data.workout_plan.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDtoConstants.FIELD_RUN_DAY
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDtoConstants.FIELD_START_DATE
import com.google.gson.annotations.SerializedName

@Entity(tableName = WorkoutPlanDtoConstants.TABLE_NAME)
data class WorkoutPlanDto(
    @PrimaryKey val id: String = "",
    @SerializedName(FIELD_START_DATE) val startDate: Long = 0L,
    val title: String = "",
    val owner: String = "",
    val description: String = "",
    val days: List<PlanDayDto> = emptyList()
)

sealed class PlanDayDto {
    data object RestDto : PlanDayDto()
    data class WorkoutSessionDto(
        val id: Int = 0,
        @SerializedName(FIELD_RUN_DAY) val runDay: Int = 0,
        val title: String = "",
        val description: String = "",
        val targets: Map<String, Float> = emptyMap()
    ) : PlanDayDto()
}
