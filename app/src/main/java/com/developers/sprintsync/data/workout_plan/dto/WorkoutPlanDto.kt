package com.developers.sprintsync.data.workout_plan.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDtoConstants.FIELD_RUN_DAY
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDtoConstants.FIELD_START_DATE
import com.google.gson.annotations.SerializedName

@Entity(tableName = WorkoutPlanDtoConstants.TABLE_NAME)
data class WorkoutPlanDto(
    @PrimaryKey val id: String,
    @SerializedName(FIELD_START_DATE) val startDate: Long,
    val title: String,
    val owner: String,
    val description: String,
    val days: List<PlanDayDto>
)

sealed class PlanDayDto {
    abstract val type: String

    data object RestDto : PlanDayDto() {
        override val type: String = WorkoutPlanDtoConstants.TYPE_REST
    }

    data class WorkoutSessionDto(
        val id: Int,
        @SerializedName(FIELD_RUN_DAY) val runDay: Int,
        val title: String,
        val description: String,
        val targets: Map<String, Float>
    ) : PlanDayDto() {
        override val type: String = WorkoutPlanDtoConstants.TYPE_WORKOUT_SESSION
    }
}