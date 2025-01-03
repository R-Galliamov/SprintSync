package com.developers.sprintsync.parameters.model

import com.developers.sprintsync.statistics.domain.goal.WellnessGoal

data class UserParameters(
    val gender: Gender,
    val birthDateTimestamp: Long,
    val weight: Float,
    val wellnessGoal: WellnessGoal,
    val useStatsPermission: Boolean,
)
