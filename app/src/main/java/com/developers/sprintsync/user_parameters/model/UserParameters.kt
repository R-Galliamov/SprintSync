package com.developers.sprintsync.user_parameters.model

import com.developers.sprintsync.statistics.domain.goal.WellnessGoal
import com.developers.sprintsync.user_parameters.components.model.Gender

data class UserParameters(
    val gender: Gender,
    val birthDateTimestamp: Long,
    val weight: Float,
    val wellnessGoal: WellnessGoal,
    val useStatsPermission: Boolean,
) {
    companion object {
        val DEFAULT =
            UserParameters(
                gender = Gender.MALE,
                birthDateTimestamp = 0L,
                weight = 70.0f,
                wellnessGoal = WellnessGoal.MAINTAIN_WEIGHT,
                useStatsPermission = true,
            )
    }
}
