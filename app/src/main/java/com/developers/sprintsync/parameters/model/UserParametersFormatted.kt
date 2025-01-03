package com.developers.sprintsync.parameters.model

import com.developers.sprintsync.statistics.domain.goal.WellnessGoal

data class UserParametersFormatted(
    val gender: Gender,
    val birthDate: String,
    val birthDateTimestamp: Long,
    val weight: String,
    val wellnessGoal: WellnessGoal,
    val useStatsPermission: Boolean,
)