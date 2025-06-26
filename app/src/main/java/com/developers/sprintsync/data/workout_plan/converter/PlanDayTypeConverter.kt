package com.developers.sprintsync.data.workout_plan.converter

import com.developers.sprintsync.data.core.SealedTypeConverter
import com.developers.sprintsync.data.workout_plan.dto.PlanDayDto

class PlanDaysTypeConverter() : SealedTypeConverter<PlanDayDto>(PlanDayDto::class.java)
