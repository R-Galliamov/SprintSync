package com.developers.sprintsync.data.workout_plan.converter

import androidx.room.TypeConverter
import com.developers.sprintsync.data.workout_plan.dto.PlanDayDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlanDayDtoListConverter {
    @TypeConverter
    fun fromList(list: List<PlanDayDto>): String = Gson().toJson(list)

    @TypeConverter
    fun toList(json: String): List<PlanDayDto> {
        val type = object : TypeToken<List<PlanDayDto>>() {}.type
        return Gson().fromJson(json, type)
    }
}
