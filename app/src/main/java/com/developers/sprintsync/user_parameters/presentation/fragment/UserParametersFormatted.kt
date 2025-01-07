package com.developers.sprintsync.user_parameters.presentation.fragment

import com.developers.sprintsync.statistics.domain.goal.WellnessGoal
import com.developers.sprintsync.user_parameters.components.model.Gender

data class UserParametersFormatted(
    val gender: Gender,
    val birthDate: String,
    val birthDateTimestamp: Long,
    val weight: String,
    val wellnessGoal: WellnessGoal,
    val useStatsPermission: Boolean,
)