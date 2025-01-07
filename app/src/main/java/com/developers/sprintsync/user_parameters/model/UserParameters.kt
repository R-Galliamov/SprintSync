package com.developers.sprintsync.user_parameters.model

import com.developers.sprintsync.statistics.domain.goal.WellnessGoal
import com.developers.sprintsync.user_parameters.components.model.Gender

data class UserParameters(
    val gender: Gender,
    val birthDateTimestamp: Long,
    val weight: Float,
    val wellnessGoal: WellnessGoal,
    val useStatsPermission: Boolean,
)
