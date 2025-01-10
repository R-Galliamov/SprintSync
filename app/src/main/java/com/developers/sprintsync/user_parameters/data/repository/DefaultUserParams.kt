package com.developers.sprintsync.user_parameters.data.repository

import com.developers.sprintsync.statistics.domain.goal.WellnessGoal
import com.developers.sprintsync.user_parameters.components.model.Gender
import com.developers.sprintsync.user_parameters.data.util.converter.GenderStorageConverter
import com.developers.sprintsync.user_parameters.data.util.converter.WellnessGoalStorageConverter
import java.util.Calendar

object DefaultUserParams {
    val DEFAULT_GENDER = GenderStorageConverter.fromGender(Gender.MALE)
    val DEFAULT_BIRTH_DATE =
        Calendar.getInstance()
            .apply {
                set(Calendar.YEAR, 2000)
                set(Calendar.MONTH, 0)
                set(Calendar.DAY_OF_MONTH, 1)
            }.timeInMillis
    const val DEFAULT_WEIGHT = 70.0f
    val DEFAULT_WELLNESS_GOAL =
        WellnessGoalStorageConverter.fromWellnessGoal(WellnessGoal.MAINTAIN_WEIGHT)
    const val DEFAULT_USE_STATS_PERMISSION = true
}