package com.developers.sprintsync.data.workout_plan.mapper

import com.developers.sprintsync.data.workout_plan.dto.PlanDayDto
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDto
import com.developers.sprintsync.domain.workouts_plan.model.Metric
import com.developers.sprintsync.domain.workouts_plan.model.PlanDay
import com.developers.sprintsync.domain.workouts_plan.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun WorkoutPlanDto.toDomain(): WorkoutPlan = WorkoutPlan(
    id = id,
    startDate = startDate,
    owner = owner,
    title = title,
    description = description,
    days = days.map { it.toDomain() }
)

fun PlanDayDto.toDomain(): PlanDay = when (this) {
    is PlanDayDto.RestDto -> PlanDay.Rest
    is PlanDayDto.WorkoutSessionDto -> PlanDay.WorkoutSession(
        id = id,
        title = title,
        description = description,
        runDay = runDay,
        targets = MetricsMapper.fromDtoMap(targets)
    )
}

// WorkoutPlan -> WorkoutPlanDto
fun WorkoutPlan.toDto(): WorkoutPlanDto = WorkoutPlanDto(
    id = id,
    startDate = startDate,
    title = title,
    owner = owner,
    description = description,
    days = days.map { it.toDto() }
)

// PlanDay -> PlanDayDto
fun PlanDay.toDto(): PlanDayDto = when (this) {
    is PlanDay.Rest -> PlanDayDto.RestDto
    is PlanDay.WorkoutSession -> PlanDayDto.WorkoutSessionDto(
        id = this.id,
        runDay = this.runDay,
        title = this.title,
        description = this.description,
        targets = MetricsMapper.toDtoMap(this.targets)
    )
}

fun List<WorkoutPlanDto>.toDomain(): List<WorkoutPlan> = map { it.toDomain() }
fun List<WorkoutPlan>.toDto(): List<WorkoutPlanDto> = map { it.toDto() }

fun Flow<List<WorkoutPlanDto>>.toDomain(): Flow<List<WorkoutPlan>> = this.map { it.toDomain() }

object MetricsMapper {

    fun toString(metric: Metric): String = metric.name.lowercase()

    private fun fromString(str: String): Metric? =
        Metric.entries.find { it.name.equals(str, ignoreCase = true) }

    fun toDtoMap(domain: Map<Metric, Float>): Map<String, Float> =
        domain.mapKeys { toString(it.key) }

    fun fromDtoMap(dto: Map<String, Float>): Map<Metric, Float> =
        dto.mapNotNull { (key, value) ->
            fromString(key)?.let { it to value }
        }.toMap()
}

fun WorkoutPlanDto.withId(id: String): WorkoutPlanDto = copy(id = id)
fun List<WorkoutPlanDto>.withId(id: String): List<WorkoutPlanDto> = map { it.withId(id) }
